package maquette.development.entities;

import lombok.AllArgsConstructor;
import maquette.core.values.UID;
import maquette.development.entities.mlflow.MlflowConfiguration;
import maquette.development.entities.mlflow.ModelCompanion;
import maquette.development.entities.mlflow.client.MlflowClient;
import maquette.development.ports.ModelsRepository;
import maquette.development.ports.models.ModelServingPort;
import maquette.development.values.exceptions.ModelNotFoundException;
import maquette.development.values.model.ModelProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class ModelEntities {

    private static final Logger LOG = LoggerFactory.getLogger(ModelEntities.class);

    private final UID workspace;

    private final MlflowClient mlflowClient;

    private final ModelsRepository models;

    private final ModelServingPort modelServingPort;

    private final ModelCompanion companion;

    public static ModelEntities apply(
        UID workspace, MlflowConfiguration mlflowConfiguration, ModelsRepository models, ModelServingPort modelServingPort) {
        return apply(workspace, MlflowClient.apply(mlflowConfiguration), models, modelServingPort,
            ModelCompanion.apply(workspace, models));
    }

    public static ModelEntities noMlflowBackend(UID workspace) {
        return apply(workspace, null, null, null, null);
    }

    public ModelEntity getModel(String model) {
        if (Objects.isNull(mlflowClient)) {
            throw ModelNotFoundException.apply(model);
        } else {
            return ModelEntity.apply(workspace, mlflowClient, models, modelServingPort, companion, model);
        }
    }

    public CompletionStage<List<ModelProperties>> getModels() {
        LOG.trace("Running `getModels` for workspace `{}`", workspace);

        /*
         * Fetch update to of MLModels in background as it may take a few minutes.
         */
        CompletableFuture.runAsync(() -> {
            if (Objects.isNull(mlflowClient)) {
                LOG.warn(
                    "MLflowClient missing `getModels` for workspace `{}` - No model information will be loaded.",
                    workspace
                );
            } else {
                try {
                    mlflowClient
                        .getModels()
                        .forEach(model -> {
                            System.out.println(model);
                            companion.mapModel(model);
                        });

                    LOG.debug("Models updated for workspace {}", workspace);
                } catch (Exception ex) {
                    LOG.warn("Unable to load models for workspace {}", workspace, ex);
                }
            }
        });

        return models
            .findAllModelsByWorkspace(workspace)
            .thenApply(models -> {
                models.sort(
                    Comparator.<ModelProperties, Instant>comparing(m -> m.getUpdated().getAt()).reversed()
                );
                return models;
            });
    }

}
