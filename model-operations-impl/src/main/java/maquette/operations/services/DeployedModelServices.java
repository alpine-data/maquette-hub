package maquette.operations.services;

import akka.Done;
import maquette.core.values.user.User;
import maquette.operations.value.DeployedModel;
import maquette.operations.value.DeployedModelProperties;
import maquette.operations.value.DeployedModelServiceInstanceProperties;
import maquette.operations.value.DeployedModelServiceProperties;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DeployedModelServices {

    CompletionStage<Done> createDeployedModel(User user, String name, String title, String url);

    CompletionStage<Done> createDeployedModelService(User user, String modelUrl,
                                                     DeployedModelServiceProperties properties);

    /**
     * Registers an existing instance of a model. This command is intended to be called during/ after
     * a deployment of a model. Thus, the call is usually initiated by a DevOps pipeline.
     *
     * @param user The user executing the action.
     * @param model Properties to identify the model.
     * @param service Properties to identify
     * @param instance
     * @return
     */
    CompletionStage<Done> registerModelServiceInstance(User user, DeployedModelProperties model,
                                                       DeployedModelServiceProperties service,
                                                       DeployedModelServiceInstanceProperties instance);


    CompletionStage<Optional<DeployedModel>> findDeployedModel(User user, String modelUrl);
}
