package maquette.adapters.projects;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.core.ports.ProjectsRepository;

public final class ProjectsRepositories {

   private ProjectsRepositories() {

   }

   public static ProjectsRepository create(ObjectMapper om) {
      var config = ProjectsRepositoryConfiguration.apply();

      switch (config.getType()) {
         case "filesystem":
         case "fs":
         case "files":
            return FileSystemProjectsRepository.apply(config.getFs(), om);
         default:
            return InMemoryProjectsRepository.apply();
      }
   }

}
