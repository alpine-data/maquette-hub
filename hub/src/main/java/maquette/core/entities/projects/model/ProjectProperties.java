package maquette.core.entities.projects.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class ProjectProperties {

    UID id;

    String name;

    String title;

    String summary;

    ActionMetadata created;

    ActionMetadata modified;

}
