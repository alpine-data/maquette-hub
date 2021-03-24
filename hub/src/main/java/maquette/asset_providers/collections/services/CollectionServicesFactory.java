package maquette.asset_providers.collections.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.asset_providers.collections.CollectionsRepository;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.data.assets.DataAssetCompanion;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionServicesFactory {

   public static CollectionServices create(RuntimeConfiguration runtime, CollectionsRepository repository) {
      var impl = CollectionServicesImpl.apply(runtime.getDataAssets(), repository);
      var comp = DataAssetCompanion.apply(runtime.getDataAssets(), runtime.getProjects());

      return CollectionServicesSecured.apply(impl, comp);
   }

}
