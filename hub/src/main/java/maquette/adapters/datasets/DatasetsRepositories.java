package maquette.adapters.datasets;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.config.RepositoryConfiguration;
import maquette.core.ports.DatasetsRepository;
import org.apache.commons.lang.NotImplementedException;

public final class DatasetsRepositories {

   private DatasetsRepositories() {

   }

   public static DatasetsRepository create(ObjectMapper om) {
      var config = RepositoryConfiguration.apply("datasets-repository");

      switch (config.getType()) {
         case "filesystem":
         case "fs":
         case "files":
            return FileSystemDatasetsRepository.apply(config.getFs(), om);

         default:
         case "in-mem":
         case "in-memory":
            throw new NotImplementedException();
      }
   }

}
