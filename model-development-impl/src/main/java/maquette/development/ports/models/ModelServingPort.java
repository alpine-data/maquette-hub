package maquette.development.ports.models;

import maquette.development.values.model.services.ModelServiceProperties;

import java.util.concurrent.CompletionStage;

public interface ModelServingPort {

    /**
     * Trigger the creation of a model.
     *
     * @param modelMlflowUrl The MLflow URL of the model (not including the version).
     * @param modelName      The name of the model.
     * @param modelVersion
     * @param serviceName    The name of the service.
     * @return The properties of the generated service.
     */
    CompletionStage<ModelServiceProperties> createModel(String modelMlflowUrl, String modelName, String modelVersion, String serviceName);

}
