package maquette.development.entities;

import akka.Done;
import akka.japi.Function;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.common.exceptions.ApplicationException;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.User;
import maquette.development.entities.mlflow.MlflowClient;
import maquette.development.entities.mlflow.ModelCompanion;
import maquette.development.ports.ModelsRepository;
import maquette.development.ports.models.ModelServingPort;
import maquette.development.values.exceptions.ModelNotFoundException;
import maquette.development.values.model.ModelMemberRole;
import maquette.development.values.model.ModelProperties;
import maquette.development.values.model.ModelVersion;
import maquette.development.values.model.ModelVersionStage;
import maquette.development.values.model.events.AutomaticallyPromoted;
import maquette.development.values.model.events.ModelVersionEvent;
import maquette.development.values.model.mlflow.ModelFromRegistry;
import maquette.development.values.model.services.ModelServiceProperties;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Entity representing a single model. All transformations within a single model instance must be done within the
 * scope of this entity.
 */
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

    /**
     * Creates a new model service instance using the model serving port.
     *
     * @param version          See {@link ModelServingPort#createModel(String, String, String, String, String, String)}.
     * @param service          See {@link ModelServingPort#createModel(String, String, String, String, String, String)}.
     * @param mlflowInstanceId See {@link ModelServingPort#createModel(String, String, String, String, String, String)}.
     * @param maintainerName   See {@link ModelServingPort#createModel(String, String, String, String, String, String)}.
     * @param maintainerEmail  See {@link ModelServingPort#createModel(String, String, String, String, String, String)}.
     * @return A set of links which can be displayed on the UI. See also
     * {@link ModelServingPort#createModel(String, String, String, String, String, String)}-
     */
    public CompletionStage<ModelServiceProperties> createService(String version, String service,
                                                                 String mlflowInstanceId, String maintainerName,
                                                                 String maintainerEmail) {
        return this.modelServing.createModel(
            this.name,
            version,
            service,
            mlflowInstanceId,
            maintainerName,
            maintainerEmail
        );
    }

    /**
     * Update model properties based upon user request/ action.
     *
     * @param executor    The user executing the action.
     * @param description The new description for the model.
     * @return Done.
     */
    public CompletionStage<Done> updateModel(User executor, String description) {
        return getProperties()
            .thenApply(model -> model
                .withDescription(description)
                .withUpdated(ActionMetadata.apply(executor)))
            .thenCompose(model -> models.insertOrUpdateModel(workspace, model));
    }

    /**
     * Update properties of a single model version.
     *
     * @param executor The user executing the action.
     * @param version  The version to be updated.
     * @param update   A function to map the previous version to a new instance with updated properties.
     * @return Done.
     */
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

    /**
     * Transfer a model version from the current stage into a new model stage.
     * The transition will be done via MLflow.
     *
     * @param executor The user executing the action.
     * @param version  The version which should be promoted.
     * @param stage    The new stage of the version.
     * @return Done.
     */
    public CompletionStage<Done> promoteModel(User executor,
                                              String version,
                                              ModelVersionStage stage) {
        return getProperties()
            .thenCompose(model -> {
                mlflowClient.transitionStage(model.getName(), version, stage.getValue());

                var updated = ActionMetadata.apply(executor);
                var updateModel = model
                    .withUpdated(updated)
                    .withVersion(model.getVersion(version).withUpdated(updated));

                return models.insertOrUpdateModel(workspace, updateModel);
            });
    }

    /**
     * Transfer a model version from the current stage into a new model stage.
     * The transition will be done via MLflow.
     *
     * Us this method only if the system does the promotion automatically. If the action
     * is triggered by a human user, use {@link ModelEntity#promoteModel(User, String, ModelVersionStage)}.
     *
     * @param version  The version which should be promoted.
     * @param stage    The new stage of the version.
     * @return Done.
     */
    public CompletionStage<Done> promoteModel(String version, ModelVersionStage stage) {
        return getProperties()
            .thenCompose(model -> {
                mlflowClient.transitionStage(model.getName(), version, stage.getValue());

                var updateModel = model
                    .withVersion(model
                        .getVersion(version)
                        .withEvent(AutomaticallyPromoted.apply(Instant.now(), stage)));

                return models.insertOrUpdateModel(workspace, updateModel);
            });
    }

    /**
     * Returns the models name.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the workspace id to which the model belongs to.
     *
     * @return The workspace id.
     */
    public UID getWorkspace() {
        return workspace;
    }

    /*
     * Manage members/ roles
     */

    /**
     * Add/ assign a user to a member role for the model.
     *
     * @param executor The user executing the action.
     * @param member   The member to be added.
     * @param role     The role the member should have for the model.
     * @return Done.
     */
    public CompletionStage<Done> addMember(User executor,
                                           UserAuthorization member,
                                           ModelMemberRole role) {
        /* TODO mw: Validate that owners/ Developers cannot assign themselves as reviewer. */
        var granted = GrantedAuthorization.apply(ActionMetadata.apply(executor), member, role);
        return models.insertOrUpdateMember(workspace, name, granted);
    }

    /**
     * Return a list of all members of the model. The function also derives standard roles based upon
     * meta-information of the model, if nothing else has been configured.
     * <p>
     * E.g., the model owner is the person who registered the model if not specified differently.
     *
     * @return The list of members.
     */
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
                        .thenCompose(done -> getMembers());
                } else {
                    return CompletableFuture.completedFuture(members);
                }
            });
    }

    /**
     * Remove a member assignment from the model.
     *
     * @param executor The user executing the action.
     * @param member   The member which should be removed.
     * @return Done.
     */
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

    /**
     * Retrieve model properties from MLflow.
     *
     * @return The model properties as retrieved from MLflow.
     */
    private CompletionStage<ModelFromRegistry> getPropertiesFromRegistry() {
        var maybeModel = mlflowClient.findModel(name);

        return maybeModel
            .<CompletionStage<ModelFromRegistry>>map(CompletableFuture::completedFuture)
            .orElseGet(() -> CompletableFuture.failedFuture(ModelNotFoundException.apply(name)));
    }

    /**
     * Specific exceptions thrown if member role assignments cannot be full-filled.
     */
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
