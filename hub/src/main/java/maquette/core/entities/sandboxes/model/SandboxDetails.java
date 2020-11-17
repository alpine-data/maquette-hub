package maquette.core.entities.sandboxes.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.infrastructure.model.DeploymentProperties;
import maquette.core.entities.processes.model.ProcessSummary;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class SandboxDetails {

   String id;

   String name;

   List<DeploymentProperties> deployments;

   List<ProcessSummary> processes;

}
