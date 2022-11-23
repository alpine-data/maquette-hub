package maquette.operations.value;

import java.util.List;

public class DeployedModel {

    /**
     * Technical name of the model.
     * This is the unique identifier for a model.
     */
    String name;

    /**
     * The human-readable name/ title of the model.
     */
    String title;

    /**
     * Model-Repository URL of the Model (usually MLFlow URL).
     */
    String url;

    /**
     * A list of registered services, which use the model.
     */
    List<DeployedModelService> services;

}
