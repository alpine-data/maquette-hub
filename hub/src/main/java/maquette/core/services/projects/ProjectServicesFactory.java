package maquette.core.services.projects;

import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.sandboxes.SandboxEntities;
import maquette.core.services.datasets.DatasetCompanion;
import maquette.core.services.sandboxes.SandboxCompanion;

public final class ProjectServicesFactory {

   private ProjectServicesFactory() {

   }

   public static ProjectServices apply(
      ProcessManager processes, ProjectEntities projects, DatasetEntities datasets, InfrastructureManager infrastructure,
      SandboxEntities sandboxes) {

      var comp = ProjectCompanion.apply(projects, datasets);
      var datasetCompanion = DatasetCompanion.apply(projects, datasets);
      var sandboxCompanion = SandboxCompanion.apply(processes, infrastructure);
      var impl = ProjectServicesImpl.apply(processes, projects, datasets, sandboxes, infrastructure, comp, datasetCompanion, sandboxCompanion);

      return ProjectServicesSecured.apply(impl, comp);
   }

}
