package maquette.core.services.data.collections;

import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.services.data.DataAssetServicesFactory;

public final class CollectionServicesFactory {

   private CollectionServicesFactory() {

   }

   public static CollectionServices apply(RuntimeConfiguration runtime) {
      var collections = runtime.getCollections();
      var projects = runtime.getProjects();

      var assets = DataAssetServicesFactory.apply(collections, runtime);
      var assetsCompanion = DataAssetCompanion.apply(collections, projects);
      var companion = CollectionCompanion.apply(assetsCompanion);
      var delegate = CollectionServicesImpl.apply(collections, assets, companion);

      return CollectionServicesSecured.apply(delegate, companion, assetsCompanion);
   }


}
