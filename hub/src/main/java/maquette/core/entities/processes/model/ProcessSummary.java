package maquette.core.entities.processes.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

@Value
@AllArgsConstructor(staticName = "apply")
public class ProcessSummary {

    int pid;

    ActionMetadata created;

    String description;

    String status;

}
