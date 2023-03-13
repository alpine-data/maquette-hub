package maquette.operations.ports;

import akka.Done;
import maquette.operations.value.DeployedModelService;
import maquette.operations.value.DeployedModelServiceInstance;
import maquette.operations.value.RegisterDeployedModelServiceParameters;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DeployedModelServicesRepository {

    /**
     * Find a registered service by its name and its Git Url.
     *
     * @param serviceName The name of the service.
     * @param gitUrl The registered Git URL of the service.
     * @return The stored information, if found.
     */
    CompletionStage<Optional<DeployedModelService>> findByNameAndGitUrl(String serviceName, String gitUrl);

    /**
     * Return a list of all model services which reference the provided model in one or more of their instances.
     *
     * @param modelUrl The unique model url (e.g., `{MLFLOW_INSTANCE_ID}/{MODEL_NAME}`.
     * @return A list of matching services.
     */
    CompletionStage<List<DeployedModelService>> findByModelUrl(String modelUrl);

    /**
     * Insert or update information of a service.
     *
     * @param service The service to insert/ update.
     * @return Done.
     */
    CompletionStage<Done> insertOrUpdate(DeployedModelService service);

    /**
     * Removes a service by its name and Git URL.
     *
     * @param serviceName The name of the service.
     * @param gitUrl The Git URL with which the service has been registered.
     * @return Done.
     */
    CompletionStage<Done> removeByNameAndGitUrl(String serviceName, String gitUrl);

}
