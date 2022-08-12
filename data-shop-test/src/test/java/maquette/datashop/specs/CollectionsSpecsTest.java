package maquette.datashop.specs;

import maquette.core.MaquetteRuntime;
import maquette.core.databind.DefaultObjectMapperFactory;
import maquette.core.databind.ObjectMapperFactory;
import maquette.datashop.configuration.FileSystemRepositoryConfiguration;
import maquette.datashop.databind.MaquetteDataShopObjectMapperFactory;
import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.ports.InMemoryDataAssetsRepository;
import maquette.datashop.providers.collections.ports.CollectionsRepository;
import maquette.datashop.providers.collections.ports.FileSystemCollectionsRepository;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public class CollectionSpecsTest extends CollectionSpecs {

    @Override
    public CollectionsRepository setupCollectionsRepository() {
        var config = FileSystemRepositoryConfiguration.apply(Path.of("./"));
        var om = DefaultObjectMapperFactory.apply().createJsonMapper();
        return FileSystemCollectionsRepository.apply(config, om);
    }

    @Override
    public DataAssetsRepository setupDataAssetsRepository() {
        return InMemoryDataAssetsRepository.apply();
    }

}
