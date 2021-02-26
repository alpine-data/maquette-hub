package maquette.core.entities.dependencies.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class UserNode implements DependencyNode {

   String id;

}
