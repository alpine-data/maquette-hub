package maquette.core.entities.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply")
public class DeploymentMemento {

    DeploymentConfig config;

    Instant created;

    DeploymentStatus status;

}
