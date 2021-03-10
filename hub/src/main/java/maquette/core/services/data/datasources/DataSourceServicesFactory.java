package maquette.core.services.data.datasources;

import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.services.data.DataAssetServicesFactory;

public final class DataSourceServicesFactory {

   private DataSourceServicesFactory() {

   }

   public static DataSourceServices apply(RuntimeConfiguration runtime) {
      var dataSources = runtime.getDataSources();
      var projects = runtime.getProjects();

      var assets = DataAssetServicesFactory.apply(dataSources, runtime);
      var assetsCompanion = DataAssetCompanion.apply(dataSources, projects);
      var companion = DataSourceCompanion.apply(assetsCompanion);
      var delegate = DataSourceServicesImpl.apply(dataSources, assets, companion);

      return DataSourceServicesSecured.apply(delegate, companion, assetsCompanion);
   }

}
