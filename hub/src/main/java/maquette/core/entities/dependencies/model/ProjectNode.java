package maquette.core.entities.dependencies.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.values.UID;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class ProjectNode implements DependencyNode {

   UID project;



}
