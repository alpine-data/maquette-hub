package maquette.adapters.projects;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.config.RepositoryConfiguration;
import maquette.core.ports.ProjectsRepository;
import org.apache.commons.lang.NotImplementedException;

public final class ProjectsRepositories {

   private ProjectsRepositories() {

   }

   public static ProjectsRepository create(ObjectMapper om) {
      var config = RepositoryConfiguration.apply("projects-repository");

      switch (config.getType()) {
         case "filesystem":
         case "fs":
         case "files":
            return FileSystemProjectsRepository.apply(config.getFs(), om);
         default:
            throw new NotImplementedException();
      }
   }

}
