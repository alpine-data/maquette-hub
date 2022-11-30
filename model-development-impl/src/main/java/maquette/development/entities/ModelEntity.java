package maquette.development.entities;

import akka.Done;
import akka.japi.Function;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.common.exceptions.ApplicationException;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.questionnaire.Answers;
import maquette.core.values.user.User;
import maquette.development.entities.mlflow.MlflowClient;
import maquette.development.entities.mlflow.ModelCompanion;
import maquette.development.ports.models.ModelServingPort;
import maquette.development.ports.ModelsRepository;
import maquette.development.values.exceptions.ModelNotFoundException;
import maquette.development.values.model.ModelMemberRole;
import maquette.development.values.model.ModelProperties;
import maquette.development.values.model.ModelVersion;
import maquette.development.values.model.events.QuestionnaireFilled;
import maquette.development.values.model.mlflow.ModelFromRegistry;
import maquette.development.values.model.services.ModelServiceProperties;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class ModelEntity {

    private final UID workspace;

    private final MlflowClient mlflowClient;

    private final ModelsRepository models;

    private final ModelServingPort modelServing;

    private final ModelCompanion companion;

    private final String name;

    public CompletionStage<ModelProperties> getProperties() {
        return getPropertiesFromRegistry().thenCompose(companion::mapModel);
    }

    public CompletionStage<ModelServiceProperties> createService(String version, String service) {
        return this.modelServing.createModel(
            this.mlflowClient.getModel(this.name).getName(), // TODO: Get Model URL?
            name, version, service);
    }

    public CompletionStage<Done> updateModel(User executor,
                                             String title,
                                             String description) {
        return getProperties()
            .thenApply(model -> model
                .withTitle(title)
                .withDescription(description)
                .withUpdated(ActionMetadata.apply(executor)))
            .thenCompose(model -> models.insertOrUpdateModel(workspace, model));
    }

    public CompletionStage<Done> updateModelVersion(User executor,
                                                    String version,
                                                    Function<ModelVersion, ModelVersion> update) {
        return getProperties()
            .thenCompose(model -> {
                var updatedVersion = Operators
                    .suppressExceptions(() -> update.apply(model.getVersion(version)))
                    .withUpdated(ActionMetadata.apply(executor));

                return models.insertOrUpdateModel(workspace, model.withVersion(updatedVersion));
            });
    }

    public CompletionStage<Done> answerQuestionnaire(User executor,
                                                     String version,
                                                     JsonNode responses) {
        return getProperties()
            .thenCompose(model -> {
                var answers = Answers.apply(ActionMetadata.apply(executor), responses);
                var updatedVersion = model.getVersion(version);
                var questionnaire = updatedVersion
                    .getQuestionnaire()
                    .withAnswers(answers);

                updatedVersion = updatedVersion
                    .withQuestionnaire(questionnaire)
                    .withEvent(QuestionnaireFilled.apply(ActionMetadata.apply(executor)))
                    .withUpdated(ActionMetadata.apply(executor));

                return models.insertOrUpdateModel(workspace, model.withVersion(updatedVersion));
            });
    }

    public CompletionStage<Done> promoteModel(User executor,
                                              String version,
                                              String stage) {
        return getProperties()
            .thenApply(model -> {
                mlflowClient.transitionStage(model.getName(), version, stage);
                return Done.getInstance();
            });
    }

    public CompletionStage<Optional<JsonNode>> getLatestQuestionnaireAnswers() {
        return getProperties()
            .thenApply(model -> model
                .getVersions()
                .stream()
                .filter(v -> v
                    .getQuestionnaire()
                    .getAnswers()
                    .isPresent())
                .sorted(Comparator
                    .<ModelVersion, Instant>comparing(v -> v
                        .getRegistered()
                        .getAt())
                    .reversed())
                .map(v -> v
                    .getQuestionnaire()
                    .getAnswers()
                    .get()
                    .getResponses())
                .findFirst());
    }

    public String getName() {
        return name;
    }

    public UID getWorkspace() {
        return workspace;
    }

    /*
     * Manage members/ roles
     */
    public CompletionStage<Done> addMember(User executor,
                                           UserAuthorization member,
                                           ModelMemberRole role) {
        var granted = GrantedAuthorization.apply(ActionMetadata.apply(executor), member, role);
        return models.insertOrUpdateMember(workspace, name, granted);
    }

    public CompletionStage<List<GrantedAuthorization<ModelMemberRole>>> getMembers() {
        return models
            .findAllMembers(workspace, name)
            .thenCompose(members -> {
                if (members.isEmpty()) {
                    return getProperties()
                        .thenApply(p -> p
                            .getCreated()
                            .getBy())
                        .thenApply(UserAuthorization::apply)
                        .thenApply(auth -> GrantedAuthorization.apply(ActionMetadata.apply(auth.getName()), auth,
                            ModelMemberRole.OWNER))
                        .thenCompose(owner -> models.insertOrUpdateMember(workspace, name, owner))
                        .thenCompose(done -> models.insertOrUpdateMember(workspace, name, GrantedAuthorization.apply(
                            ActionMetadata.apply("alice"), UserAuthorization.apply("alice"),
                            ModelMemberRole.REVIEWER))) // TODO get default review from Project configuration.
                        .thenCompose(done -> getMembers());
                } else {
                    return CompletableFuture.completedFuture(members);
                }
            });
    }

    public CompletionStage<Done> removeMember(User executor,
                                              UserAuthorization member) {
        return models
            .findAllMembers(workspace, name)
            .thenCompose(members -> {
                var isOwnerRemoved = members
                    .stream()
                    .anyMatch(auth -> auth
                        .getAuthorization()
                        .equals(member) &&
                        auth
                            .getRole()
                            .equals(ModelMemberRole.OWNER) &&
                        auth
                            .getAuthorization()
                            .authorizes(executor));
                if (isOwnerRemoved) {
                    throw MembersException.userCannotRemoveSelf();
                }
                return models.removeMember(workspace, name, member);
            });
    }

    private CompletionStage<ModelFromRegistry> getPropertiesFromRegistry() {
        var maybeModel = mlflowClient.findModel(name);

        return maybeModel
            .<CompletionStage<ModelFromRegistry>>map(CompletableFuture::completedFuture)
            .orElseGet(() -> CompletableFuture.failedFuture(ModelNotFoundException.apply(name)));
    }

    public static class MembersException extends ApplicationException {

        private MembersException(String message) {
            super(message);
        }

        public static MembersException userCannotRemoveSelf() {
            var msg = "You cannot revoke your own access.";
            return new MembersException(msg);
        }

        public static MembersException invalidOwner() {
            var msg = "Only users are allowed to be owners of a data asset.";
            return new MembersException(msg);
        }

    }

}
