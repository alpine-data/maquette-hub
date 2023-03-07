package maquette.operations.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.values.user.User;
import maquette.operations.entities.DeployedModelServiceEntities;
import maquette.operations.ports.ModelDevelopmentPort;
import maquette.operations.value.DeployedModelService;
import maquette.operations.value.RegisterDeployedModelServiceInstanceParameters;
import maquette.operations.value.RegisterDeployedModelServiceParameters;

import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DeployedModelServicesImpl implements DeployedModelServices {

    private final DeployedModelServiceEntities entities;

    private final ModelDevelopmentPort modelDevelopmentPort;

    @Override
    public CompletionStage<Done> registerModelServiceInstance(
        User user, RegisterDeployedModelServiceParameters service, RegisterDeployedModelServiceInstanceParameters instance) {

        return entities
            .registerModelServiceInstance(service, instance)
            // Also inform model development about the registration.
            .thenCompose(done -> modelDevelopmentPort.modelDeployedEvent(service, instance));
    }

    @Override
    public CompletionStage<List<DeployedModelService>> findDeployedModelServicesByModelUrl(User user, String modelUrl) {
        return entities.findServicesByModelUrl(modelUrl);
    }

}
