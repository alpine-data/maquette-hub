package maquette.operations.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * A value class to store information about a deployed model version. Each service instance can contain multiple
 * models.
 */
@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class DeployedModelVersion {

    /**
     * The URL of the model. By default, this is `${MLFLOW_INSTANCE_ID}/${MODEL_NAME}`.
     */
    String modelUrl;

    /**
     * The version which is deployed.
     */
    String modelVersion;

}
