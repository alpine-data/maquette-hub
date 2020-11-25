package maquette.core.services.projects;

import maquette.core.entities.data.datasets.Datasets;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.Projects;

public final class ProjectServicesFactory {

   private ProjectServicesFactory() {

   }

   public static ProjectServices apply(
      ProcessManager processes, Projects projects, Datasets datasets, InfrastructureManager infrastructure) {

      var comp = ProjectCompanion.apply(projects, datasets);
      var impl = ProjectServicesImpl.apply(processes, projects, datasets, infrastructure, comp);

      return ProjectServicesSecured.apply(impl, comp);
   }

}
