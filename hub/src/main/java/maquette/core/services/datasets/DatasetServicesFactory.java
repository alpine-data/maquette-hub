package maquette.core.services.datasets;

import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.ProjectEntities;

public final class DatasetServicesFactory {

   private DatasetServicesFactory() {

   }

   public static DatasetServices apply(ProjectEntities projects, DatasetEntities datasets, ProcessManager processes) {
      var comp = DatasetCompanion.apply(projects, datasets);
      var impl = DatasetServicesImpl.apply(datasets, projects, comp, processes);

      return DatasetServicesSecured.apply(impl, comp);
   }

}
