package maquette.core.services.dependencies.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.dependencies.model.DependencyNode;
import maquette.core.entities.projects.model.apps.Application;
import maquette.core.values.UID;

@Value
@AllArgsConstructor(staticName = "apply")
public class ApplicationPropertiesNode implements DependencyPropertiesNode {

   UID project;

   UID id;

   Application properties;

}
