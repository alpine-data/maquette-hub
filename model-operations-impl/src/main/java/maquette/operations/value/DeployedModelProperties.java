package maquette.operations.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class DeployedModelProperties {

    /**
     * Technical name of the model.
     * This is the unique identifier for a model.
     */
    String name;

    /**
     * Model-Repository URL of the Model (usually MLFlow URL).
     */
    String url;

}
