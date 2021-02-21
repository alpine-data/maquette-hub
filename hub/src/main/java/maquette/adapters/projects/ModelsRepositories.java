package maquette.adapters.projects;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.config.RepositoryConfiguration;
import maquette.core.entities.projects.ports.ModelsRepository;
import org.apache.commons.lang.NotImplementedException;

public final class ModelsRepositories {

   private ModelsRepositories() {

   }

   public static ModelsRepository create(ObjectMapper om) {
      var config = RepositoryConfiguration.apply("models-repository");

      switch (config.getType()) {
         case "filesystem":
         case "fs":
         case "files":
            return FileSystemModelsRepository.apply(config.getFs(), om);
         default:
            throw new NotImplementedException();
      }
   }

}
