package maquette.asset_providers.sources.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.asset_providers.sources.DataSourceEntities;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.data.DataAssetCompanion;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataSourceServicesFactory {

   public static DataSourceServices create(RuntimeConfiguration runtime, DataSourceEntities entities) {
      var comp = DataAssetCompanion.apply(runtime.getDataAssets(), runtime.getProjects(), runtime.getDataAssetProviders());
      var impl = DataSourceServicesImpl.apply(runtime, entities);

      return DataSourceServicesSecured.apply(impl, comp);
   }

}
