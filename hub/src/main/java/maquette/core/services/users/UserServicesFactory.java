package maquette.core.services.users;

import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasources.DataSourceEntities;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.users.Users;
import maquette.core.services.data.DataAssetCompanion;

public final class UserServicesFactory {

   private UserServicesFactory() {

   }

   public static UserServices apply(ProjectEntities projects, DatasetEntities datasets, DataSourceEntities dataSources, Users users) {
      var comp = UserCompanion.apply(users);
      var datasetCompanion = DataAssetCompanion.apply(datasets, projects);
      var dataSourceCompanion = DataAssetCompanion.apply(dataSources, projects);
      return UserServicesImpl.apply(datasets, dataSources, projects, comp, datasetCompanion, dataSourceCompanion);
   }

}
