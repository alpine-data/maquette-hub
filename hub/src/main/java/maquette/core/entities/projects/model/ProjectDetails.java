package maquette.core.entities.projects.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;
import maquette.core.values.authorization.GrantedAuthorization;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class ProjectDetails {

    String id;

    String name;

    ActionMetadata created;

    ActionMetadata modified;

    List<GrantedAuthorization> authorizations;

}
