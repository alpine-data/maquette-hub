package maquette.core.entities.dependencies.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class UserNode implements DependencyNode {

   String id;

}
