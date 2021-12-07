package maquette.datashop.providers.datasets.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.providers.datasets.ports.DatasetDataExplorer;
import maquette.datashop.providers.datasets.ports.DatasetsRepository;
import maquette.datashop.services.DataAssetServicesCompanion;
import maquette.datashop.ports.WorkspacesServicePort;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatasetServicesFactory {

   public static DatasetServices apply(MaquetteRuntime runtime, DatasetsRepository repository, DatasetDataExplorer dataExplorer, WorkspacesServicePort workspaces) {
      var shop = runtime.getModule(MaquetteDataShop.class);
      var comp = DataAssetServicesCompanion.apply(shop.getEntities(), workspaces);
      var impl = DatasetServicesImpl.apply(repository, dataExplorer, shop.getEntities());
      return DatasetServicesSecured.apply(impl, comp);
   }

}
