package maquette.core.services.data.datasets;

import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.services.data.DataAssetServicesFactory;
import maquette.core.services.dependencies.DependencyCompanion;

public final class DatasetServicesFactory {

   private DatasetServicesFactory() {

   }

   public static DatasetServices apply(RuntimeConfiguration runtime) {
      var datasets = runtime.getDatasets();
      var projects = runtime.getProjects();
      var processes = runtime.getProcessManager();
      var dependencies = runtime.getDependencies();

      var assets = DataAssetServicesFactory.apply(datasets, projects);
      var assetsCompanion = DataAssetCompanion.apply(datasets, projects);
      var dependenciesCompanion = DependencyCompanion.apply(
         dependencies, projects, datasets,
         runtime.getCollections(), runtime.getDataSources(), runtime.getStreams());

      var comp = DatasetCompanion.apply(assetsCompanion);
      var impl = DatasetServicesImpl.apply(datasets, processes, assets, comp);
      var sec = DatasetServicesSecured.apply(impl, comp, assetsCompanion);
      var logged = DatasetServicesLogged.apply(datasets, sec, runtime.getLogs(), dependenciesCompanion);

      return DatasetServicesValidated.apply(logged);
   }

}
