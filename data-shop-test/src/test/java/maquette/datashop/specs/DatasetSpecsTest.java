package maquette.datashop.specs;

import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.ports.InMemoryDataAssetsRepository;
import maquette.datashop.providers.datasets.ports.DatasetsRepository;
import maquette.datashop.providers.datasets.ports.InMemoryDatasetsRepository;

public class DatasetSpecsTest extends DatasetSpecs {

    @Override
    DataAssetsRepository setupDataAssetsRepository() {
        return InMemoryDataAssetsRepository.apply();
    }

    @Override
    DatasetsRepository setupDatasetsRepository() {
        return InMemoryDatasetsRepository.apply();
    }

}
