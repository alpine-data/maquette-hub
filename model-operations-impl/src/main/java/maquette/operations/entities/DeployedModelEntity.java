package maquette.operations.entities;

import akka.Done;
import maquette.operations.value.DeployedModelServiceProperties;
import maquette.operations.value.EDeployedModelServiceStatus;

import java.util.concurrent.CompletionStage;

public class DeployedModelEntity {

    String name;

    public CompletionStage<Done> registerModelService(DeployedModelServiceProperties properties) {
        // Add a new service to the database.
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
     * @param url              The URL of the service.
     * @param environment      The environment to which the service is deployed.
     * @return Done.
     */
    public CompletionStage<Done> registerModelServiceInstance(String modelServiceName, String url, String version,
                                                              String environment) {
        return null;
    }

    /**
     * Checks and updates the status of the model instance if status has changed.
     *
     * @param modelServiceName The unique name of the service.
     * @param url              The url of the instance.
     * @param status           The detected status.
     * @return Done.
     */
    public CompletionStage<Done> updateModelServiceInstanceService(String modelServiceName, String url,
                                                                   EDeployedModelServiceStatus status) {
        return null;
    }

    public CompletionStage<Done> removeRegisteredModelService(String name) {
        // Remove information about all instances.
        // Remove service from database.
        return null;
    }

}
