package maquette.adapters.projects;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.config.RepositoryConfiguration;
import maquette.core.entities.projects.ports.ApplicationsRepository;
import org.apache.commons.lang.NotImplementedException;

public final class ApplicationsRepositories {

   private ApplicationsRepositories() {

   }

   public static ApplicationsRepository create(ObjectMapper om) {
      var config = RepositoryConfiguration.apply("applications-repository");

      switch (config.getType()) {
         case "filesystem":
         case "fs":
         case "files":
            return FileSystemApplicationsRepository.apply(config.getFs(), om);
         default:
            throw new NotImplementedException();
      }
   }

}
