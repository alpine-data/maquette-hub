package maquette.operations.ports;

import akka.Done;
import maquette.operations.value.RegisterDeployedModelServiceInstanceParameters;
import maquette.operations.value.RegisterDeployedModelServiceParameters;

import java.util.concurrent.CompletionStage;

/**
 * Port/ Adapter to communicate with Model development module.
 */
public interface ModelDevelopmentPort {

    /**
     * This method is used by Model Operations module to inform the Model development module about deployment of a model version.
     *
     * @param service Information about the service which has been deployed.
     * @param instance Information about the instance of a service.
     * @return Done.
     */
    CompletionStage<Done> modelDeployedEvent(
        RegisterDeployedModelServiceParameters service,
        RegisterDeployedModelServiceInstanceParameters instance);

}
