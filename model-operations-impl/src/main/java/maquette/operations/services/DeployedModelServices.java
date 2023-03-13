package maquette.operations.services;

import akka.Done;
import maquette.core.values.user.User;
import maquette.operations.value.DeployedModelService;
import maquette.operations.value.RegisterDeployedModelServiceInstanceParameters;
import maquette.operations.value.RegisterDeployedModelServiceParameters;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Services to manage deployed model services. A model is always related to one model. A model is identified by a
 * model URL.
 */
public interface DeployedModelServices {

    /**
     * Registers an existing instance of a model. This command is intended to be called during/ after
     * a deployment of a model. Thus, the call is usually initiated by a DevOps pipeline.
     *
     * @param user     The user executing the action.
     * @param service  Properties to identify the service.
     * @param instance Properties of the instance which has been deployed.
     * @return
     */
    CompletionStage<Done> registerModelServiceInstance(
        User user, RegisterDeployedModelServiceParameters service, RegisterDeployedModelServiceInstanceParameters instance);

    /**
     * Get a list of registered model services (and instances) for a provided model.
     *
     * @param user The user requesting the information.
     * @param modelUrl The unique model URL. Usually this has the form of `${MLFLOW_INSTANCE_ID}/${MODEL_NAME}`.
     * @return A list of registered services for the model.
     */
    CompletionStage<List<DeployedModelService>> findDeployedModelServicesByModelUrl(User user, String modelUrl);
}
