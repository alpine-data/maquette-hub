package maquette.asset_providers.datasets.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.asset_providers.datasets.DatasetsRepository;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.ports.DataExplorer;
import maquette.core.ports.RecordsStore;
import maquette.core.services.data.assets.DataAssetCompanion;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatasetServicesFactory {

   public static DatasetServices apply(RuntimeConfiguration runtime, DatasetsRepository repository, RecordsStore recordsStore, DataExplorer dataExplorer) {
      var comp = DataAssetCompanion.apply(runtime.getDataAssets(), runtime.getProjects());
      var impl = DatasetServicesImpl.apply(repository, recordsStore, dataExplorer, runtime.getDataAssets());
      return DatasetServicesSecured.apply(impl, comp);
   }

}
