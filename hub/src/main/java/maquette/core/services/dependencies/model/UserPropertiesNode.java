package maquette.core.services.dependencies.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.users.model.UserProfile;

@Value
@AllArgsConstructor(staticName = "apply")
public class UserPropertiesNode implements DependencyPropertiesNode {

   String id;

   UserProfile properties;

}
