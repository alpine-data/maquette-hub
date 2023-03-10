package maquette.adapters;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.adapters.configuration.MaquetteAdaptersConfiguration;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.development.MaquetteModelDevelopment;
import maquette.development.entities.ModelEntity;
import maquette.development.entities.WorkspaceEntity;
import maquette.development.values.model.ModelVersionStage;
import maquette.operations.ports.ModelDevelopmentPort;
import maquette.operations.value.RegisterDeployedModelServiceInstanceParameters;
import maquette.operations.value.RegisterDeployedModelServiceParameters;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class MaquetteModelDevelopmentAdapter implements ModelDevelopmentPort {

    /**
     * Maquette Runtime. Model Development module is only accessed within functions because during initialisation
     * of this adapter this module might not be available.
     */
    private final MaquetteRuntime runtime;

    private final MaquetteAdaptersConfiguration configuration;

    public static MaquetteModelDevelopmentAdapter apply(MaquetteRuntime runtime) {
        return apply(runtime, MaquetteAdaptersConfiguration.apply());
    }

    /**
     * When Model Operations informs about a model deployment (initiated by a
     * {@link maquette.operations.commands.RegisterDeployedModelServiceInstanceCommand}), then we check whether the
     * model needs to be promoted in MLflow to a new stage.
     *
     * @param service  Information about the service which has been deployed.
     * @param instance Information about the instance of a service.
     * @return Done.
     */
    @Override
    public CompletionStage<Done> modelDeployedEvent(RegisterDeployedModelServiceParameters service,
                                                    RegisterDeployedModelServiceInstanceParameters instance) {

        var updates = instance
            .getModels()
            .stream()
            .map(modelVersion -> {
                // Model Url is by default `{MLFLOW_INSTANCE_ID}/{MODEL_NAME}"
                var mlflowId = modelVersion.getModelUrl().split("/")[0];
                var modelName = modelVersion.getModelUrl().split("/")[1];

                // Check whether reported stage should automatically promote model to staging
                var promoteToStaging = configuration
                    .getPromoteModelToStagingForEnvironments()
                    .stream()
                    .anyMatch(env -> env.equalsIgnoreCase(instance.getEnvironment()));

                // Check whether reported stage should automatically promote model to production
                var promoteToProduction = configuration
                    .getPromoteModelToProductionForEnvironments()
                    .stream()
                    .anyMatch(env -> env.equalsIgnoreCase(instance.getEnvironment()));

                /*
                 * Update model version if required.
                 */
                var modelEntityCS = runtime
                    .getModule(MaquetteModelDevelopment.class)
                    .getWorkspaces()
                    .getWorkspaceByMlflowId(mlflowId)
                    .thenCompose(WorkspaceEntity::getModels)
                    .thenApply(modelEntities -> modelEntities.getModel(modelName));

                var modelPropertiesCS = modelEntityCS
                    .thenCompose(ModelEntity::getProperties);

                return Operators.compose(modelEntityCS, modelPropertiesCS, (modelEntity, modelProperties) -> {
                        var currentStage = modelProperties.getVersion(modelVersion.getModelVersion()).getStage();

                        if (promoteToStaging && currentStage.equals(ModelVersionStage.NONE)) {
                            return modelEntity.promoteModel(modelVersion.getModelVersion(), ModelVersionStage.STAGING);
                        } else if (promoteToProduction && (currentStage.equals(ModelVersionStage.NONE) || currentStage.equals(ModelVersionStage.STAGING))) {
                            return modelEntity.promoteModel(modelVersion.getModelVersion(),
                                ModelVersionStage.PRODUCTION);
                        } else {
                            return CompletableFuture.completedFuture(Done.getInstance());
                        }
                    })
                    .thenCompose(cs -> cs);
            });

        return Operators.allOf(updates).thenApply(results -> Done.getInstance());
    }

}
