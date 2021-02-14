package maquette.core.services.users;

import maquette.core.entities.data.collections.CollectionEntities;
import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasources.DataSourceEntities;
import maquette.core.entities.data.streams.StreamEntities;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.users.UserEntities;
import maquette.core.services.data.DataAssetCompanion;

public final class UserServicesFactory {

   private UserServicesFactory() {

   }

   public static UserServices apply(ProjectEntities projects, CollectionEntities collections, DatasetEntities datasets, DataSourceEntities dataSources, StreamEntities streams, UserEntities users) {
      var comp = UserCompanion.apply(users);
      var collectionCompanion = DataAssetCompanion.apply(collections, projects);
      var datasetCompanion = DataAssetCompanion.apply(datasets, projects);
      var dataSourceCompanion = DataAssetCompanion.apply(dataSources, projects);
      var streamCompanion = DataAssetCompanion.apply(streams, projects);
      return UserServicesImpl.apply(
         collections, datasets, dataSources, streams, projects, users, comp,
         collectionCompanion, datasetCompanion, dataSourceCompanion, streamCompanion);
   }

}
