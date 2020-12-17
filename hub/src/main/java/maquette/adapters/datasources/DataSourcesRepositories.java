package maquette.adapters.datasources;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.config.RepositoryConfiguration;
import maquette.core.ports.DataSourcesRepository;
import org.apache.commons.lang.NotImplementedException;

public final class DataSourcesRepositories {

   private DataSourcesRepositories() {

   }

   public static DataSourcesRepository create(ObjectMapper om) {
      var config = RepositoryConfiguration.apply("sources-repository");

      switch (config.getType()) {
         case "filesystem":
         case "fs":
         case "files":
            return FileSystemDataSourcesRepository.apply(config.getFs(), om);

         default:
         case "in-mem":
         case "in-memory":
            throw new NotImplementedException();
      }
   }

}
