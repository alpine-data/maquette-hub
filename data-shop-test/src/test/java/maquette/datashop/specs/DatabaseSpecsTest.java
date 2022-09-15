package maquette.datashop.specs;

import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.ports.InMemoryDataAssetsRepository;

public class DatabaseSpecsTest extends DatabaseSpecs {

    @Override
    public DataAssetsRepository setupDataAssetsRepository() {
        return InMemoryDataAssetsRepository.apply();
    }

}
