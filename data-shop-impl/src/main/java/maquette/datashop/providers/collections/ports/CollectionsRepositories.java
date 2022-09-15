package maquette.datashop.providers.collections.ports;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.datashop.configuration.RepositoryConfiguration;
import org.apache.commons.lang3.NotImplementedException;

public final class CollectionsRepositories {

    private CollectionsRepositories() {
    }

    /**
     * Creates a CollectionsRepository based on the configured repository type.
     * Implemented repository types:
     * - filesystem
     *
     * @param om The ObjectMapper which serializes Java objects into JSON.
     * @return The created CollectionsRepository.
     */
    public static CollectionsRepository create(ObjectMapper om) {
        var config = RepositoryConfiguration.apply("data-repository");

        switch (config.getType()) {
            case "filesystem":
            case "fs":
            case "files":
                return FileSystemCollectionsRepository.apply(config.getFs(), om);

            default:
            case "in-mem":
            case "in-memory":
                throw new NotImplementedException();
        }
    }

}
