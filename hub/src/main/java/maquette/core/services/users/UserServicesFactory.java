package maquette.core.services.users;

import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.users.Users;
import maquette.core.services.datasets.DatasetCompanion;

public final class UserServicesFactory {

   private UserServicesFactory() {

   }

   public static UserServices apply(ProjectEntities projects, DatasetEntities datasets, Users users) {
      var comp = UserCompanion.apply(users);
      var datasetCompanion = DatasetCompanion.apply(projects, datasets);
      return UserServicesImpl.apply(datasets, projects, comp, datasetCompanion);
   }

}
