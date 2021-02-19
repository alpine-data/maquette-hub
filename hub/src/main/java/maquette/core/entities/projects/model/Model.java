package maquette.core.entities.projects.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;
import maquette.core.values.authorization.GrantedAuthorization;

import java.util.List;
import java.util.Set;

@Value
@AllArgsConstructor(staticName = "apply")
public class Model {

   String title;

   String name;

   Set<String> flavours;

   String description;

   int warnings;

   List<GrantedAuthorization<ModelMemberRole>> team;

   List<ModelVersion> versions;

   ActionMetadata created;

   ActionMetadata updated;

}
