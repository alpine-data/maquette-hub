package maquette.asset_providers.collections.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.asset_providers.collections.CollectionsRepository;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.services.dependencies.DependencyCompanion;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionServicesFactory {

   public static CollectionServices create(RuntimeConfiguration runtime, CollectionsRepository repository) {
      var dependencies = DependencyCompanion.apply(runtime);
      var assets = DataAssetCompanion.apply(runtime.getDataAssets(), runtime.getProjects(), runtime.getDataAssetProviders(), dependencies);
      var impl = CollectionServicesImpl.apply(runtime.getDataAssets(), repository);
      var comp = DataAssetCompanion.apply(runtime.getDataAssets(), runtime.getProjects(), runtime.getDataAssetProviders(), dependencies);
      var logged = CollectionServicesLogged.apply(impl, runtime.getDataAssets(), runtime.getLogs(), assets);

      return CollectionServicesSecured.apply(logged, comp);
   }

}
