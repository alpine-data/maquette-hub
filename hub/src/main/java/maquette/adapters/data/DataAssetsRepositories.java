package maquette.adapters.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.config.RepositoryConfiguration;
import maquette.core.entities.data.ports.DataAssetsRepository;
import org.apache.commons.lang.NotImplementedException;

public final class DataAssetsRepositories {

   private DataAssetsRepositories() {

   }

   public static DataAssetsRepository create(ObjectMapper om) {
      var config = RepositoryConfiguration.apply("data-repository");

      switch (config.getType()) {
         case "filesystem":
         case "fs":
         case "files":
            return FileSystemDataAssetsRepository.apply(config.getFs(), om);

         default:
         case "in-mem":
         case "in-memory":
            throw new NotImplementedException();
      }
   }

}
