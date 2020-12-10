package maquette.core.services.data.datasources;

import maquette.core.entities.data.datasources.DataSourceEntities;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.services.data.DataAssetServicesFactory;

public final class DataSourceServicesFactory {

   private DataSourceServicesFactory() {

   }

   public static DataSourceServices apply(DataSourceEntities dataSources, ProjectEntities projects) {
      var assets = DataAssetServicesFactory.apply(dataSources, projects);
      var assetsCompanion = DataAssetCompanion.apply(dataSources, projects);
      var companion = DataSourceCompanion.apply(assetsCompanion);
      var delegate = DataSourceServicesImpl.apply(dataSources, assets, companion);

      return DataSourceServicesSecured.apply(delegate, companion, assetsCompanion);
   }

}
