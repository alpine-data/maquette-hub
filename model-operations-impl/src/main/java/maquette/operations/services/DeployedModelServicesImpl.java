package maquette.operations.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.values.user.User;
import maquette.operations.entities.DeployedModelServiceEntities;
import maquette.operations.ports.ModelDevelopmentPort;
import maquette.operations.value.DeployedModelService;
import maquette.operations.value.RegisterDeployedModelServiceInstanceParameters;
import maquette.operations.value.RegisterDeployedModelServiceParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DeployedModelServicesImpl implements DeployedModelServices {

    private static final Logger LOG = LoggerFactory.getLogger(DeployedModelServices.class);

    private final DeployedModelServiceEntities entities;

    private final ModelDevelopmentPort modelDevelopmentPort;

    @Override
    public CompletionStage<Done> registerModelServiceInstance(
        User user, RegisterDeployedModelServiceParameters service,
        RegisterDeployedModelServiceInstanceParameters instance) {

        return entities
            .registerModelServiceInstance(service, instance)
            .thenApply(done -> {
                /*
                 * Also inform model development about the registration.
                 * We spawn this as a separate thread, because the result is not important for model registration.
                 */
                CompletableFuture.runAsync(() -> modelDevelopmentPort
                    .modelDeployedEvent(service, instance)
                    .exceptionally(exc -> {
                        LOG.warn("An exception occurred while notifying model development about a new registered service instance.");
                        return Done.getInstance();
                    })
                );

                return done;
            });
    }

    @Override
    public CompletionStage<List<DeployedModelService>> findDeployedModelServicesByModelUrl(User user, String modelUrl) {
        return entities.findServicesByModelUrl(modelUrl);
    }

}
