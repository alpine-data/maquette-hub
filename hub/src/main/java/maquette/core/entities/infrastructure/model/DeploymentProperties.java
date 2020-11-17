package maquette.core.entities.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class DeploymentProperties {

   DeploymentConfig config;

   List<ContainerProperties> properties;

   Instant created;

   DeploymentStatus status;

}
