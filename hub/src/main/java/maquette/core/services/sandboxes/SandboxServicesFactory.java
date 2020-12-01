package maquette.core.services.sandboxes;

import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.sandboxes.SandboxEntities;
import maquette.core.services.projects.ProjectCompanion;

public final class SandboxServicesFactory {

   private SandboxServicesFactory() {

   }

   public static SandboxServices apply(ProcessManager processes, InfrastructureManager infrastructure, ProjectEntities projects, SandboxEntities sandboxes, DatasetEntities datasets) {
      var companion = SandboxCompanion.apply(processes, infrastructure);
      var projectCompanion = ProjectCompanion.apply(projects, datasets);
      var impl = SandboxServicesImpl.apply(processes, infrastructure, projects, sandboxes, companion);
      return SandboxServicesSecured.apply(impl, projectCompanion);
   }

}
