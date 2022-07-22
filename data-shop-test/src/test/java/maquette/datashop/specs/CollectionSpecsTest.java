package maquette.datashop.specs;

import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.ports.InMemoryDataAssetsRepository;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class CollectionSpecsTest extends CollectionSpecs {

    @Override
    public DataAssetsRepository setupDataAssetsRepository() {
        return InMemoryDataAssetsRepository.apply();
    }

}
