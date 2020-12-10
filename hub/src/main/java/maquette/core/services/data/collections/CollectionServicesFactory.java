package maquette.core.services.data.collections;

import maquette.core.entities.data.collections.CollectionEntities;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.services.data.DataAssetServicesFactory;

public final class CollectionServicesFactory {

   private CollectionServicesFactory() {

   }

   public static CollectionServices apply(CollectionEntities collections, ProjectEntities projects) {
      var assets = DataAssetServicesFactory.apply(collections, projects);
      var assetsCompanion = DataAssetCompanion.apply(collections, projects);
      var companion = CollectionCompanion.apply(assetsCompanion);
      var delegate = CollectionServicesImpl.apply(collections, assets, companion);

      return CollectionServicesSecured.apply(delegate, companion, assetsCompanion);
   }


}
