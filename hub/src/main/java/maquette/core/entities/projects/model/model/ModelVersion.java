package maquette.core.entities.projects.model.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.entities.projects.model.model.actions.*;
import maquette.core.entities.projects.model.questionnaire.Questionnaire;
import maquette.core.values.ActionMetadata;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class ModelVersion {

   String version;

   String description;

   ActionMetadata registered;

   ActionMetadata updated;

   Set<String> flavours;

   String stage;

   Questionnaire questionnaire;

   List<String> codeQualityIssues;

   boolean merged;

   String gitCommit;

   String gitTransferUrl;

   ActionMetadata approved;

   public static ModelVersion apply(
      String version, String description, ActionMetadata registered, Set<String> flavours, String stage, Questionnaire questionnaire) {
      return apply(version, description, registered, registered, flavours, stage, questionnaire, List.of(), false, null, null, null);
   }

   @JsonProperty("actions")
   public List<ModelAction> getActions() {
      var actions = Lists.<ModelAction>newArrayList();

      if (questionnaire.getAnswers().isEmpty()) {
         actions.add(FillQuestionnaire.apply());
      } else {
         actions.add(ReviewQuestionnaire.apply());
      }

      if (getApproved().isEmpty() && questionnaire.getAnswers().isPresent()) {
         actions.add(ApproveModel.apply());
      }

      if (!Objects.isNull(gitTransferUrl)) {
         actions.add(ViewSource.apply(gitTransferUrl));
      }

      if (!stage.equals("Archived")) {
         if (stage.equals("None")) {
            actions.add(PromoteModel.apply("Staging"));
         }

         if ((stage.equals("Staging") || stage.equals("None")) && getApproved().isPresent()) {
            actions.add(PromoteModel.apply("Production"));
         }
      }

      if (!stage.equals("Archived")) {
         actions.add(ArchiveModel.apply());
      } else {
         actions.add(RestoreModel.apply());
      }

      return actions;
   }

   @JsonProperty("actions")
   @SuppressWarnings("unused")
   private void setActions(List<ModelAction> actions) {
      // ignore
   }

   public Optional<ActionMetadata> getApproved() {
      return Optional.ofNullable(approved);
   }

   public Optional<String> getGitCommit() {
      return Optional.ofNullable(gitCommit);
   }

   public Optional<String> getGitTransferUrl() {
      return Optional.ofNullable(gitTransferUrl);
   }

}
