package maquette.development.ports.models;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface ModelOperationsPort {

    /**
     * Return metadata of services which use a model.
     *
     * @param modelUrl The MLflow URL of the model.
     * @return A list of services (metadata) which use this model.
     *
     * The metadata should contain links to service which are using this model.
     */
    CompletionStage<List<Object>> getServices(String modelUrl);

}
