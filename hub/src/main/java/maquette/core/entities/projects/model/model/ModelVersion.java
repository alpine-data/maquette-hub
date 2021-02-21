package maquette.core.entities.projects.model.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.entities.projects.model.questionnaire.Questionnaire;
import maquette.core.values.ActionMetadata;

import java.util.List;
import java.util.Optional;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class ModelVersion {

   String version;

   String description;

   ActionMetadata registered;

   ActionMetadata updated;

   String stage;

   Questionnaire questionnaire;

   List<String> codeQualityIssues;

   boolean merged;

   String gitCommit;

   String gitTransferUrl;

   ActionMetadata approved;

   public static ModelVersion apply(
      String version, String description, ActionMetadata registered, String stage, Questionnaire questionnaire) {
      return apply(version, description, registered, registered, stage, questionnaire, List.of(), false, null, null, null);
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
