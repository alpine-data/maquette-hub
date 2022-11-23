package maquette.operations.value;

import java.time.Instant;

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
