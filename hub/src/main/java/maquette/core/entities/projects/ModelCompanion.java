package maquette.core.entities.projects;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import maquette.common.ObjectMapperFactory;
import maquette.common.Operators;
import maquette.core.entities.projects.model.model.ModelFromRegistry;
import maquette.core.entities.projects.model.model.ModelProperties;
import maquette.core.entities.projects.model.model.ModelVersion;
import maquette.core.entities.projects.model.model.governance.GitDetails;
import maquette.core.entities.projects.model.questionnaire.Questionnaire;
import maquette.core.entities.projects.ports.ModelsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class ModelCompanion {

   private final UID project;

   private final ModelsRepository models;

   public CompletionStage<ModelProperties> mapModel(ModelFromRegistry registeredModel) {
      return models
         .findModelByName(project, registeredModel.getName())
         .thenApply(maybeModel -> {
            var title = registeredModel.getName();
            var name = registeredModel.getName();
            var flavors = registeredModel
               .getVersions()
               .stream()
               .flatMap(version -> version.getFlavors().stream())
               .collect(Collectors.toSet());
            var createdBy = registeredModel
               .getVersions()
               .get(registeredModel.getVersions().size() - 1)
               .getUser()
               .replace("michaelwellner", "michael"); // TODO ensure right user names tracked by MLflow.

            var created = ActionMetadata.apply(createdBy, registeredModel.getCreated());

            var updatedBy = registeredModel
               .getVersions()
               .get(0)
               .getUser()
               .replace("michaelwellner", "michael"); // TODO ensure right user names tracked by MLflow.

            var updatedTime = registeredModel.getVersions().get(0).getCreated();
            var updated = ActionMetadata.apply(updatedBy, updatedTime);

            var description = "";
            var defaultQuestionnaire = getQuestionnaire();

            var versions = registeredModel
               .getVersions()
               .stream()
               .map(v -> {
                  var registered = ActionMetadata.apply(
                     v.getUser().replace("michaelwellner", "michael"), // TODO ensure right user names tracked by MLflow.
                     v.getCreated());

                  var version = ModelVersion.apply(
                     v.getVersion(), v.getDescription(),
                     registered, v.getFlavors(), v.getStage(), defaultQuestionnaire);

                  if (v.getGitCommit().isPresent()) {
                     var gitDetails = GitDetails.apply(v.getGitCommit().orElse(null), v.getGitUrl().orElse(null), false);
                     version = version.withGitDetails(gitDetails);
                  }

                  return version;
               })
               .collect(Collectors.toList());

            var merged = maybeModel
               .orElse(ModelProperties.apply(title, name, flavors, description, List.of(), versions, created, updated))
               .withFlavours(flavors);

            var versionsMap = merged
               .getVersions()
               .stream()
               .collect(Collectors.toMap(ModelVersion::getVersion, v -> v));

            merged = merged.withVersions(versions
               .stream()
               .map(versionFromRegistry -> {
                  if (versionsMap.containsKey(versionFromRegistry.getVersion())) {
                     var v = versionsMap.get(versionFromRegistry.getVersion());

                     if (versionFromRegistry.getGitDetails().isPresent() && v.getGitDetails().isPresent() && v.getGitDetails().get().isMaster()) {
                        v = v.withGitDetails(versionFromRegistry.getGitDetails().get().withMaster(true));
                     } else {
                        v = v.withGitDetails(versionFromRegistry.getGitDetails().orElse(null));
                     }

                     return v
                        .withStage(versionFromRegistry.getStage())
                        .withFlavours(versionFromRegistry.getFlavours());
                  } else {
                     return versionFromRegistry;
                  }
               })
               .collect(Collectors.toList()));

            return merged;
         });
   }

   private Questionnaire getQuestionnaire() {
      // TODO make path configurable
      var questionnairePath = new File("/Users/michaelwellner/Workspaces/maquette-hub/hub/config/model-questionnaire.json");
      var questions = Operators.suppressExceptions(() -> ObjectMapperFactory.apply().create().readValue(questionnairePath, JsonNode.class));
      return Questionnaire.apply(questions);
   }

}
