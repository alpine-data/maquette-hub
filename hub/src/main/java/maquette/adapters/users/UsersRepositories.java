package maquette.adapters.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.adapters.projects.FileSystemProjectsRepository;
import maquette.adapters.projects.InMemoryProjectsRepository;
import maquette.adapters.projects.ProjectsRepositoryConfiguration;
import maquette.core.ports.ProjectsRepository;
import maquette.core.ports.UsersRepository;

public final class UsersRepositories {

   private UsersRepositories() {

   }

   public static UsersRepository create(ObjectMapper om) {
      var config = UsersRepositoryConfiguration.apply();

      switch (config.getType()) {
         case "filesystem":
         case "fs":
         case "files":
            return FileSystemUsersRepository.apply(config.getFs(), om);
         default:
            return InMemoryUsersRepository.apply();
      }
   }

}
