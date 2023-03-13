package maquette.development.ports.models;

import maquette.development.values.model.services.ModelServiceProperties;

import java.util.concurrent.CompletionStage;

public interface ModelServingPort {

    /**
     * Trigger the creation of model
     *
     * @param modelName        the name of the model
     * @param modelVersion     the version of the model
     * @param serviceName      the service name of the model
     * @param mlflowInstanceId the MLFlow instance ID
     * @param maintainerName   the name of the maintainer
     * @param maintainerEmail  the e-mail of the maintainer
     * @return Model.
     */
    CompletionStage<ModelServiceProperties> createModel(
        String modelName,
        String modelVersion,
        String serviceName,
        String mlflowInstanceId,
        String maintainerName,
        String maintainerEmail
    );

}
