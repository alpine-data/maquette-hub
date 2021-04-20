package maquette.core.entities.projects.model.sandboxes.stacks;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class DeployedStackProperties {

   String deployment;

   StackConfiguration configuration;

}
