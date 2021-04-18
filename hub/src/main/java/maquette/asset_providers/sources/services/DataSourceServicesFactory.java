package maquette.asset_providers.sources.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.asset_providers.sources.DataSourceEntities;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.services.dependencies.DependencyCompanion;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataSourceServicesFactory {

   public static DataSourceServices create(RuntimeConfiguration runtime, DataSourceEntities entities) {
      var dependencies = DependencyCompanion.apply(runtime);
      var comp = DataAssetCompanion.apply(runtime.getDataAssets(), runtime.getProjects(), runtime.getDataAssetProviders(), dependencies);
      var impl = DataSourceServicesImpl.apply(runtime, entities);
      var logged = DataSourceServicesLogged.apply(impl, runtime.getDataAssets(), runtime.getLogs(), comp);

      return DataSourceServicesSecured.apply(logged, comp);
   }

}
