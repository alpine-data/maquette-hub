package maquette.core.entities.projects;

import lombok.AllArgsConstructor;
import maquette.core.entities.projects.model.MlflowConfiguration;
import maquette.core.entities.projects.model.Model;
import maquette.core.entities.projects.model.ModelVersion;
import maquette.core.entities.projects.ports.MlflowPort;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class ModelEntities {

   private static final Logger LOG = LoggerFactory.getLogger(ModelEntities.class);

   private final UID project;

   private final MlflowConfiguration mlflowConfiguration;

   public CompletionStage<List<Model>> getModels() {
      var mlflowPort = MlflowPort.apply(mlflowConfiguration, project);

      return CompletableFuture
         .supplyAsync(() -> mlflowPort
            .getModels()
            .stream()
            .map(registeredModel -> {
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
                  .getUser();
               var created = ActionMetadata.apply(createdBy, registeredModel.getCreated());
               var updatedBy = registeredModel.getVersions().get(0).getUser();

               var updatedTime = registeredModel.getVersions().get(0).getCreated();
               var updated = ActionMetadata.apply(updatedBy, updatedTime);

               var description = "";
               var warnings = 0;

               var versions = registeredModel
                  .getVersions()
                  .stream()
                  .map(v -> {
                     var registered = ActionMetadata.apply(v.getUser(), v.getCreated());
                     return ModelVersion.apply(
                        v.getVersion(), v.getDescription(),
                        registered, registered, v.getStage());
                  })
                  .collect(Collectors.toList());

               return Model.apply(title, name, flavors, description, warnings, List.of(), versions, created, updated);
            })
            .collect(Collectors.toList()))
         .exceptionally(ex -> {
            LOG.warn("Unable to load models for project {}", project, ex);
            return List.of();
         });
   }

}
