package maquette.core.entities.projects.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class ProjectSummary {

    String id;

    String name;

    ActionMetadata created;

    ActionMetadata modified;

}
