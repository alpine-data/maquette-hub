package maquette.datashop.providers.collections.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.ports.WorkspacesServicePort;
import maquette.datashop.providers.collections.ports.CollectionsRepository;
import maquette.datashop.services.DataAssetServicesCompanion;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionServicesFactory {

   public static CollectionServices apply(MaquetteRuntime runtime, CollectionsRepository repository, WorkspacesServicePort workspaces) {
      var dataAssets = runtime.getModule(MaquetteDataShop.class).getEntities();

      var comp = DataAssetServicesCompanion.apply(dataAssets, workspaces);
      var impl = CollectionServicesImpl.apply(dataAssets, repository);

      return CollectionServicesSecured.apply(impl, comp);
   }

}
