package maquette.development.values.model.mlflow;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;
import java.util.List;

/**
 * Represents model information as extracted from MLflow.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class ModelFromRegistry {

    /**
     * The name of the model as tracked in MLflow.
     */
    String name;

    /**
     * The moment when the model was registered.
     */
    Instant created;

    /**
     * The moment the model was last updated in MLflow.
     */
    Instant updated;

    /**
     * A list of version of the model.
     */
    List<VersionFromRegistry> versions;

}
