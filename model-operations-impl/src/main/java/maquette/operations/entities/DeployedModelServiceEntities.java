package maquette.operations.entities;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.operations.ports.DeployedModelServicesRepository;
import maquette.operations.value.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Container for operations across all/ multiple entities.
 *
 * As of now we haven't created a separate Entity class, because there are not enough operations on a single entity. But
 * this could be required in the future.
 */
@AllArgsConstructor(staticName = "apply")
public final class DeployedModelServiceEntities {

    /**
     * The repository/ database to store service information.
     */
    private final DeployedModelServicesRepository deployedModelServicesRepository;

    /**
     * Find service instances based on a model URL. The function should return all services which have registered
     * instances including the specified model.
     *
     * @param modelUrl The URL of the model. Usually in form of `{MLFLOW_INSTANCE_ID}/{NAME}`.
     * @return A list of services containing the model.
     */
    public CompletionStage<List<DeployedModelService>> findServicesByModelUrl(String modelUrl) {
        return deployedModelServicesRepository.findByModelUrl(modelUrl);
    }

    /**
     * Find an already registered service instance.
     *
     * @param name The name of the service.
     * @param gitUrl The registered Git repository Url of the service.
     * @return The entity for the service, if found. Otherwise, nothing.
     */
    public CompletionStage<Optional<DeployedModelService>> findServiceByNameAndGitUrl(String name, String gitUrl) {
        return deployedModelServicesRepository.findByNameAndGitUrl(name, gitUrl);
    }

    /**
     * Registers a new model service instance. If the service does not exist yet, it will be registered as well.
     *
     * @param service Service properties as send to Maquette.
     * @param instance Service instance properties as send to Maquette.
     * @return Done.
     */
    public CompletionStage<Done> registerModelServiceInstance(RegisterDeployedModelServiceParameters service, RegisterDeployedModelServiceInstanceParameters instance) {
        return deployedModelServicesRepository
            .findByNameAndGitUrl(service.getName(), service.getGitRepositoryUrl())
            .thenCompose(maybeModelService -> {
                var newInstance = DeployedModelServiceInstance.apply(
                    instance.getUrl(), instance.getEnvironment(),
                    DeployedModelServiceInstanceStatus.AVAILABLE, Instant.now(), instance.getModels());

                DeployedModelService modelService;

                if (maybeModelService.isPresent()) {
                    modelService = maybeModelService
                        .get()
                        .withBackstageCatalogUrl(service.getCatalogUrl())
                        .withInstance(newInstance);
                } else {
                    modelService = DeployedModelService.apply(
                        service.getName(), service.getGitRepositoryUrl(),
                        service.getCatalogUrl(), List.of(newInstance));
                }

                return deployedModelServicesRepository.insertOrUpdate(modelService);
            });
    }
}
