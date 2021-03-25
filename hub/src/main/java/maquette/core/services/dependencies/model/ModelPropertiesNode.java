package maquette.core.services.dependencies.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.projects.model.model.ModelProperties;
import maquette.core.values.UID;

@Value
@AllArgsConstructor(staticName = "apply")
public class ModelPropertiesNode implements DependencyPropertiesNode {

   UID project;

   String name;

   ModelProperties properties;

}
