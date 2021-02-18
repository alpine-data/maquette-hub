package maquette.core.services.projects;

import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasources.DataSourceEntities;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.sandboxes.SandboxEntities;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.services.data.datasets.DatasetCompanion;
import maquette.core.services.data.datasources.DataSourceCompanion;
import maquette.core.services.sandboxes.SandboxCompanion;

public final class ProjectServicesFactory {

   private ProjectServicesFactory() {

   }

   public static ProjectServices apply(
      ProcessManager processes,
      ProjectEntities projects,
      DatasetEntities datasets,
      DataSourceEntities dataSources,
      InfrastructureManager infrastructure,
      SandboxEntities sandboxes) {

      var comp = ProjectCompanion.apply(projects, datasets, infrastructure);
      var datasetCompanion = DatasetCompanion.apply(DataAssetCompanion.apply(datasets, projects));
      var datasourceCompanion = DataSourceCompanion.apply(DataAssetCompanion.apply(dataSources, projects));
      var sandboxCompanion = SandboxCompanion.apply(processes, infrastructure);
      var impl = ProjectServicesImpl.apply(
         processes, projects, datasets, dataSources, sandboxes, infrastructure,
         comp, datasetCompanion, datasourceCompanion, sandboxCompanion);

      return ProjectServicesSecured.apply(impl, comp);
   }

}
