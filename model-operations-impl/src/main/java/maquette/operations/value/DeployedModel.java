package maquette.operations.value;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.List;

@With
@Value
@AllArgsConstructor(staticName = "apply")
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
