package maquette.development.entities;

import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.UID;
import maquette.development.entities.mlflow.MlflowClient;
import maquette.development.entities.mlflow.MlflowConfiguration;
import maquette.development.entities.mlflow.ModelCompanion;
import maquette.development.ports.ModelsRepository;
import maquette.development.values.exceptions.ModelNotFoundException;
import maquette.development.values.model.ModelProperties;
import maquette.development.values.stacks.MlflowStackConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class ModelEntities {

   private static final Logger LOG = LoggerFactory.getLogger(ModelEntities.class);

   private final UID workspace;

   private final MlflowClient mlflowClient;

   private final ModelsRepository models;

   private final ModelCompanion companion;

   public static ModelEntities apply(
       UID workspace, MlflowConfiguration mlflowConfiguration, ModelsRepository models) {
      return apply(workspace, MlflowClient.apply(mlflowConfiguration, workspace), models, ModelCompanion.apply(workspace, models));
   }

   public static ModelEntities noMlflowBackend(UID workspace) {
      return apply(workspace, null, null);
   }

   public ModelEntity getModel(String model) {
      if (Objects.isNull(mlflowClient)) {
         throw ModelNotFoundException.apply(model);
      } else {
         return ModelEntity.apply(workspace, mlflowClient, models, companion, model);
      }
   }

   public CompletionStage<List<ModelProperties>> getModels() {
      if (Objects.isNull(mlflowClient)) {
         return CompletableFuture.completedFuture(List.of());
      } else {
         return CompletableFuture
             .supplyAsync(() -> mlflowClient
                 .getModels()
                 .stream()
                 .map(companion::mapModel)
                 .collect(Collectors.toList()))
             .thenCompose(Operators::allOf)
             .exceptionally(ex -> {
                LOG.warn("Unable to load models for workspace {}", workspace, ex);
                return List.of();
             });
      }
   }

}
