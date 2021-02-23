package maquette.core.entities.projects;

import akka.Done;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import maquette.core.entities.projects.exceptions.ModelNotFoundException;
import maquette.core.entities.projects.model.model.ModelProperties;
import maquette.core.entities.projects.model.model.ModelFromRegistry;
import maquette.core.entities.projects.model.model.ModelMemberRole;
import maquette.core.entities.projects.model.model.ModelVersion;
import maquette.core.entities.projects.model.questionnaire.Answers;
import maquette.core.entities.projects.ports.MlflowPort;
import maquette.core.entities.projects.ports.ModelsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.exceptions.DomainException;
import maquette.core.values.user.User;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class ModelEntity {

   private final UID project;

   private final MlflowPort mlflowPort;

   private final ModelsRepository models;

   private final ModelCompanion companion;


   private final String name;

   public CompletionStage<ModelProperties> getProperties() {
      return getPropertiesFromRegistry().thenCompose(companion::mapModel);
   }

   public CompletionStage<Done> updateModel(User executor, String title, String description) {
      return getProperties()
         .thenApply(model -> model
            .withTitle(title)
            .withDescription(description)
            .withUpdated(ActionMetadata.apply(executor)))
         .thenCompose(model -> models.insertOrUpdateModel(project, model));
   }

   public CompletionStage<Done> answerQuestionnaire(User executor, String version, JsonNode responses) {
      return getProperties()
         .thenCompose(model -> {
            var answers = Answers.apply(ActionMetadata.apply(executor), responses);
            var updatedVersion = model.getVersion(version);
            var questionnaire = updatedVersion
               .getQuestionnaire()
               .withAnswers(answers);

            updatedVersion = updatedVersion
               .withQuestionnaire(questionnaire)
               .withUpdated(ActionMetadata.apply(executor));

            return models.insertOrUpdateModel(project, model.withVersion(updatedVersion));
         });
   }

   public CompletionStage<Done> approveModel(User executor, String version) {
      return getProperties()
         .thenCompose(model -> {
            var updatedVersion = model
               .getVersion(version)
               .withApproved(ActionMetadata.apply(executor))
               .withUpdated(ActionMetadata.apply(executor));

            return models.insertOrUpdateModel(project, model.withVersion(updatedVersion));
         });
   }

   public CompletionStage<Done> promoteModel(User executor, String version, String stage) {
      return getProperties()
         .thenApply(model -> {
            mlflowPort.transitionStage(model.getName(), version, stage);
            return Done.getInstance();
         });
   }

   public CompletionStage<Optional<JsonNode>> getLatestQuestionnaireAnswers() {
      return getProperties()
         .thenApply(model -> model
            .getVersions()
            .stream()
            .filter(v -> v.getQuestionnaire().getAnswers().isPresent())
            .sorted(Comparator.<ModelVersion, Instant>comparing(v -> v.getRegistered().getAt()).reversed())
            .map(v -> v.getQuestionnaire().getAnswers().get().getResponses())
            .findFirst());
   }

   /*
    * Manage members/ roles
    */
   public CompletionStage<Done> addMember(User executor, UserAuthorization member, ModelMemberRole role) {
      var granted = GrantedAuthorization.apply(ActionMetadata.apply(executor), member, role);
      return models.insertOrUpdateMember(project, name, granted);
   }

   public CompletionStage<List<GrantedAuthorization<ModelMemberRole>>> getMembers() {
      return models.findAllMembers(project, name);
   }

   public CompletionStage<Done> removeMember(User executor, UserAuthorization member) {
      return models.removeMember(project, name, member);
   }

   private CompletionStage<ModelFromRegistry> getPropertiesFromRegistry() {
      var maybeModel = mlflowPort.findModel(name);

      return maybeModel
         .<CompletionStage<ModelFromRegistry>>map(CompletableFuture::completedFuture)
         .orElseGet(() -> CompletableFuture.failedFuture(ModelNotFoundException.apply(name)));
   }

   public static class MembersException extends RuntimeException implements DomainException {

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
