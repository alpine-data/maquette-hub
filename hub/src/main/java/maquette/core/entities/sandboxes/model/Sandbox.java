package maquette.core.entities.sandboxes.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.processes.model.ProcessSummary;
import maquette.core.entities.sandboxes.model.stacks.DeployedStackDetails;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class Sandbox {

   UID id;

   String name;

   ActionMetadata created;

   List<DeployedStackDetails> stacks;

   List<ProcessSummary> processes;

}
