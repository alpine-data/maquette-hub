package maquette.core.services.users;

import maquette.core.entities.projects.Projects;
import maquette.core.entities.users.Users;

public final class UserServicesFactory {

   private UserServicesFactory() {

   }

   public static UserServices apply(Projects projects, Users users) {
      var comp = UserCompanion.apply(users);
      return UserServicesImpl.apply(projects, comp);
   }

}