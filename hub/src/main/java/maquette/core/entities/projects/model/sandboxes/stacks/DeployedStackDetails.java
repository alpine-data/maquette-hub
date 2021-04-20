package maquette.core.entities.projects.model.sandboxes.stacks;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.infrastructure.model.DeploymentProperties;

@Value
@AllArgsConstructor(staticName = "apply")
public class DeployedStackDetails {

   DeploymentProperties deployment;

   StackConfiguration configuration;

   DeployedStackParameters parameters;

}
