package maquette.operations.value;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.time.Instant;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class DeployedModelServiceStatus {

    /**
     * The current status as detected by the system.
     */
    EDeployedModelServiceStatus status;

    /**
     * The point in time when it was detected.
     */
    Instant since;

}
