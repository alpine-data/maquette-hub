package maquette.core.services.sandboxes;

import maquette.core.entities.data.datasets.Datasets;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.Projects;
import maquette.core.entities.sandboxes.Sandboxes;
import maquette.core.services.projects.ProjectCompanion;

public final class SandboxServicesFactory {

   private SandboxServicesFactory() {

   }

   public static SandboxServices apply(ProcessManager processes, InfrastructureManager infrastructure, Projects projects, Sandboxes sandboxes, Datasets datasets) {
      var comp = ProjectCompanion.apply(projects, datasets);
      var impl = SandboxServicesImpl.apply(processes, infrastructure, projects, sandboxes);
      return SandboxServicesSecured.apply(impl, comp);
   }

}
