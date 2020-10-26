package maquette.adapters.datasets;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.core.ports.DatasetsRepository;

public final class DatasetsRepositories {

   private DatasetsRepositories() {

   }

   public static DatasetsRepository create(ObjectMapper om) {
      var config = DatasetsRepositoryConfiguration.apply();

      switch (config.getType()) {
         case "filesystem":
         case "fs":
         case "files":
            return FileSystemDatasetsRepository.apply(config.getFs(), om);

         default:
         case "in-mem":
         case "in-memory":
            return InMemoryDatasetsRepository.apply();
      }
   }

}
