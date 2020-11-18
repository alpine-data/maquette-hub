package maquette.core.entities.sandboxes.model.stacks;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class DeployedStackProperties {

   String deployment;

   StackConfiguration configuration;

}
