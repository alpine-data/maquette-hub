package maquette.core.entities.sandboxes.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.processes.model.ProcessSummary;
import maquette.core.entities.sandboxes.model.stacks.DeployedStackDetails;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class SandboxDetails {

   String id;

   String name;

   List<DeployedStackDetails> stacks;

   List<ProcessSummary> processes;

}