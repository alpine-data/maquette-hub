package maquette.core.entities.sandboxes.model.stacks;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.infrastructure.model.DeploymentProperties;

@Value
@AllArgsConstructor(staticName = "apply")
public class DeployedStackDetails {

   DeploymentProperties deployment;

   StackConfiguration configuration;

}
