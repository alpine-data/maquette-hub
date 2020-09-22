package maquette.core.entities.project.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

@Value
@AllArgsConstructor(staticName = "apply")
public class ProjectSummary {

    String id;

    String name;

    ActionMetadata created;

    ActionMetadata modified;

}
