package maquette.operations.value;

import lombok.*;

import java.time.Instant;

@With
@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
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
