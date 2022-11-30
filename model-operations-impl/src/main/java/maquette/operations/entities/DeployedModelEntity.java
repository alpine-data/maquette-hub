package maquette.operations.entities;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.exceptions.ApplicationException;
import maquette.operations.ports.DeployedModelServicesRepository;
import maquette.operations.value.*;

import java.time.Instant;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class DeployedModelEntity {

    String url;

    private final DeployedModelServicesRepository deployedModelServicesRepository;

    public CompletionStage<Done> registerModelService(DeployedModelServiceProperties properties) {
        // Add a new service to the database.
        return deployedModelServicesRepository.insertOrUpdate(properties);
    }

    public CompletionStage<Done> registerModelServiceInstance(DeployedModelServiceProperties service, DeployedModelServiceInstanceProperties instance) {
        // TODO: Implement.
        return null;
    }

    /**
     * Register or update the version of a model service instance.
     * <p>
     * Inserts the model instance in the database if not present. If present the version and environment
     * get updated.
     *
     * @param modelServiceName The unique name of the service.
     * @param version          The current model version which is deployed in that instance.
     * @param instanceUrl      The URL of the instance.
     * @param environment      The environment to which the service is deployed.
     * @return Done.
     */
    public CompletionStage<Done> registerModelServiceInstance(String modelServiceName, String instanceUrl, String version,
                                                              String environment) {
        return deployedModelServicesRepository.findInstanceByUrl(modelServiceName, instanceUrl)
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
                        DeployedModelServiceInstance.apply(instanceUrl, version, environment,
                            DeployedModelServiceStatus.apply(EDeployedModelServiceStatus.NOT_AVAILABLE, Instant.now()))
                    ))
            );
    }

    /**
     * Checks and updates the status of the model instance if status has changed.
     *
     * @param modelServiceName The unique name of the service.
     * @param instanceUrl      The url of the instance.
     * @param status           The detected status.
     * @return Done.
     * @throws InstanceException If instance doesn't exist.
     */
    public CompletionStage<Done> updateModelServiceInstance(String modelServiceName, String instanceUrl,
                                                            EDeployedModelServiceStatus status) {
        return deployedModelServicesRepository.findInstanceByUrl(modelServiceName, instanceUrl)
            .thenCompose(inst ->
                inst.map(instance ->
                        deployedModelServicesRepository.insertOrUpdateInstance(modelServiceName,
                            instance.withStatus(DeployedModelServiceStatus.apply(status, Instant.now()))))
                    .orElseThrow(InstanceException::instanceNotFound));
    }

    public CompletionStage<Done> removeRegisteredModelService(String serviceName) {
        return deployedModelServicesRepository
            .removeByName(serviceName)
            .thenCompose((status) -> deployedModelServicesRepository.removeAllInstances(serviceName));
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
