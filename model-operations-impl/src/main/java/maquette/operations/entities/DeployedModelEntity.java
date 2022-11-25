package maquette.operations.entities;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.exceptions.ApplicationException;
import maquette.operations.ports.DeployedModelServicesRepository;
import maquette.operations.value.DeployedModelInstance;
import maquette.operations.value.DeployedModelServiceProperties;
import maquette.operations.value.DeployedModelServiceStatus;
import maquette.operations.value.EDeployedModelServiceStatus;

import java.time.Instant;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class DeployedModelEntity {

    String name;

    private final DeployedModelServicesRepository deployedModelServicesRepository;

    public CompletionStage<Done> registerModelService(DeployedModelServiceProperties properties) {
        // Add a new service to the database.
        return deployedModelServicesRepository.insertOrUpdate(properties);
    }

    /**
     * Register or update the version of a model service instance.
     * <p>
     * Inserts the model instance in the database if not present. If present the version and environment
     * get updated.
     *
     * @param modelServiceName The unique name of the service.
     * @param version          The current model version which is deployed in that instance.
     * @param url              The URL of the service.
     * @param environment      The environment to which the service is deployed.
     * @return Done.
     */
    public CompletionStage<Done> registerModelServiceInstance(String modelServiceName, String url, String version,
                                                              String environment) {
        return deployedModelServicesRepository.findInstanceByUrl(modelServiceName, url)
            .thenCompose(inst ->
                inst
                    .map(instance ->
                        deployedModelServicesRepository.insertOrUpdateInstance(
                            modelServiceName,
                            instance
                                .withModelVersion(version)
                                .withEnvironment(environment)
                        )
                    )
                    .orElseGet(() -> deployedModelServicesRepository.insertOrUpdateInstance(
                        modelServiceName,
                        DeployedModelInstance.apply(url, version, environment,
                            DeployedModelServiceStatus.apply(EDeployedModelServiceStatus.NOT_AVAILABLE, Instant.now()))
                    ))
            );
    }

    /**
     * Checks and updates the status of the model instance if status has changed.
     *
     * @param modelServiceName The unique name of the service.
     * @param url              The url of the instance.
     * @param status           The detected status.
     * @return Done.
     * @throws InstanceException If instance doesn't exist.
     */
    public CompletionStage<Done> updateModelServiceInstance(String modelServiceName, String url,
                                                            EDeployedModelServiceStatus status) {
        return deployedModelServicesRepository.findInstanceByUrl(modelServiceName, url)
            .thenCompose(inst ->
                inst.map(instance ->
                        deployedModelServicesRepository.insertOrUpdateInstance(modelServiceName,
                            instance.withStatus(DeployedModelServiceStatus.apply(status, Instant.now()))))
                    .orElseThrow(InstanceException::instanceNotFound));
    }

    public CompletionStage<Done> removeRegisteredModelService(String name) {
        return deployedModelServicesRepository
            .removeByName(name)
            .thenCompose((status) -> deployedModelServicesRepository.removeAllInstances(name));
    }

    public static class InstanceException extends ApplicationException {
        private InstanceException(String message) {
            super(message);
        }

        public static InstanceException instanceNotFound() {
            return new InstanceException("instance not found");
        }
    }

}
