package maquette.adapters.collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.config.RepositoryConfiguration;
import maquette.core.ports.CollectionsRepository;
import org.apache.commons.lang.NotImplementedException;

public final class CollectionsRepositories {

   private CollectionsRepositories() {

   }

   public static CollectionsRepository create(ObjectMapper om) {
      var config = RepositoryConfiguration.apply("collections-repository");

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
