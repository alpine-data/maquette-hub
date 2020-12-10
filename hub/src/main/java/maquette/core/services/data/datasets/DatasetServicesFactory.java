package maquette.core.services.data.datasets;

import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.services.data.DataAssetServicesFactory;

public final class DatasetServicesFactory {

   private DatasetServicesFactory() {

   }

   public static DatasetServices apply(ProjectEntities projects, DatasetEntities datasets, ProcessManager processes) {
      var assets = DataAssetServicesFactory.apply(datasets, projects);
      var assetsCompanion = DataAssetCompanion.<DatasetProperties, DatasetEntities>apply(datasets, projects);
      var comp = DatasetCompanion.apply(assetsCompanion);
      var impl = DatasetServicesImpl.apply(datasets, processes, assets, comp);

      return DatasetServicesSecured.apply(impl, comp, assetsCompanion);
   }

}
