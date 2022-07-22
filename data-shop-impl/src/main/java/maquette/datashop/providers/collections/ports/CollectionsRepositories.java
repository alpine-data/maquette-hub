package maquette.datashop.providers.collections.ports;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.datashop.configuration.RepositoryConfiguration;
import org.apache.commons.lang3.NotImplementedException;

public final class CollectionsRepositories {

   private CollectionsRepositories() {

   }

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
