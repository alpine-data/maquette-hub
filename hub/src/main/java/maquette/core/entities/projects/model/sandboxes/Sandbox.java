package maquette.core.entities.projects.model.sandboxes;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.processes.model.ProcessSummary;
import maquette.core.entities.infrastructure.model.DataVolume;
import maquette.core.entities.projects.model.sandboxes.stacks.DeployedStackDetails;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class Sandbox {

   UID id;

   UID project;

   DataVolume volume;

   String name;

   ActionMetadata created;

   List<DeployedStackDetails> stacks;

   List<ProcessSummary> processes;

}
