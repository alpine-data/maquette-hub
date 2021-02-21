package maquette.core.entities.projects;

import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.projects.model.MlflowConfiguration;
import maquette.core.entities.projects.model.model.ModelProperties;
import maquette.core.entities.projects.ports.MlflowPort;
import maquette.core.entities.projects.ports.ModelsRepository;
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

   private final MlflowPort mlflowPort;

   private final ModelsRepository models;

   private final ModelCompanion companion;

   public static ModelEntities apply(
      UID project, MlflowConfiguration mlflowConfiguration, ModelsRepository models) {
      return apply(project, MlflowPort.apply(mlflowConfiguration, project), models, ModelCompanion.apply(project, models));
   }

   public ModelEntity getModel(String model) {
      return ModelEntity.apply(project, mlflowPort, models, companion, model);
   }

   public CompletionStage<List<ModelProperties>> getModels() {
      return CompletableFuture
         .supplyAsync(() -> mlflowPort
            .getModels()
            .stream()
            .map(companion::mapModel)
            .collect(Collectors.toList()))
         .thenCompose(Operators::allOf)
         .exceptionally(ex -> {
            LOG.warn("Unable to load models for project {}", project, ex);
            return List.of();
         });
   }

}
