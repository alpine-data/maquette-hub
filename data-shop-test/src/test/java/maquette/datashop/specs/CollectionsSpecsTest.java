package maquette.datashop.specs;

import maquette.core.databind.DefaultObjectMapperFactory;
import maquette.datashop.configuration.FileSystemRepositoryConfiguration;
import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.ports.InMemoryDataAssetsRepository;
import maquette.datashop.providers.collections.ports.CollectionsRepository;
import maquette.datashop.providers.collections.ports.FileSystemCollectionsRepository;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CollectionsSpecsTest extends CollectionSpecs {

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

    @Override
    public Path getResourcePath() {
        return Paths.get("./src/main/resources/");
    }

}
