package maquette.core.services.users;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.collections.CollectionEntities;
import maquette.core.entities.data.collections.model.CollectionProperties;
import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasources.DataSourceEntities;
import maquette.core.entities.data.datasources.model.DataSourceProperties;
import maquette.core.entities.data.streams.StreamEntities;
import maquette.core.entities.data.streams.model.StreamProperties;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.users.UserEntity;
import maquette.core.entities.users.model.UserNotification;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import org.apache.commons.compress.utils.Lists;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class UserServicesImpl implements UserServices {

   private final CollectionEntities collections;

   private final DatasetEntities datasets;

   private final DataSourceEntities dataSources;

   private final StreamEntities streams;

   private final ProjectEntities projects;

   private final UserCompanion companion;

   private final DataAssetCompanion<CollectionProperties, CollectionEntities> collectionCompanion;

   private final DataAssetCompanion<DatasetProperties, DatasetEntities> datasetCompanion;

   private final DataAssetCompanion<DataSourceProperties, DataSourceEntities> dataSourceCompanion;

   private final DataAssetCompanion<StreamProperties, StreamEntities> streamCompanion;

   /*
    * Notifications
    */

   @Override
   public CompletionStage<UserProfile> getProfile(User executor, String userId) {
      return companion
         .withUser(userId)
         .thenCompose(UserEntity::getProfile);
   }

   @Override
   public CompletionStage<Done> updateUserDetails(User executor, String base64encodedDetails) {
      return companion
         .withUser(executor)
         .thenCompose(entity -> entity.updateUserDetails(base64encodedDetails));
   }

   @Override
   public CompletionStage<List<UserNotification>> getNotifications(User executor) {
      return companion.withUserOrDefault(
         executor,
         Lists.newArrayList(),
         UserEntity::getNotifications);
   }

   @Override
   public CompletionStage<Done> readNotification(User executor, String notificationId) {
      return companion.withUserOrDefault(
         executor,
         Done.getInstance(),
         user -> user.readNotification(notificationId));
   }

   /*
    * Assets
    */

   @Override
   public CompletionStage<List<ProjectProperties>> getProjects(User user) {
      return projects.getProjectsByMember(user);
   }

   @Override
   public CompletionStage<List<DataAssetProperties<?>>> getDataAssets(User user) {
      var collectionsCS = collections
         .list()
         .thenApply(assets -> assets
            .stream()
            .map(properties -> collectionCompanion.filterMember(user, properties.getName(), properties)))
         .thenCompose(Operators::allOf)
         .thenApply(assets -> assets
            .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(properties -> (DataAssetProperties<?>) properties)
            .collect(Collectors.toList()));

      var datasetsCS = datasets
         .list()
         .thenApply(assets -> assets
            .stream()
            .map(properties -> datasetCompanion.filterMember(user, properties.getName(), properties)))
         .thenCompose(Operators::allOf)
         .thenApply(assets -> assets
            .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(properties -> (DataAssetProperties<?>) properties)
            .collect(Collectors.toList()));

      var dataSourcesCS = dataSources
         .list()
         .thenApply(assets -> assets
            .stream()
            .map(properties -> dataSourceCompanion.filterMember(user, properties.getName(), properties)))
         .thenCompose(Operators::allOf)
         .thenApply(assets -> assets
            .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(properties -> (DataAssetProperties<?>) properties)
            .collect(Collectors.toList()));

      var streamsCS = streams
         .list()
         .thenApply(assets -> assets
            .stream()
            .map(properties -> streamCompanion.filterMember(user, properties.getName(), properties)))
         .thenCompose(Operators::allOf)
         .thenApply(assets -> assets
            .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(properties -> (DataAssetProperties<?>) properties)
            .collect(Collectors.toList()));

      return Operators.compose(collectionsCS, datasetsCS, dataSourcesCS, streamsCS, (collections, sets, sources, streams) -> {
         var result = Lists.<DataAssetProperties<?>>newArrayList();
         result.addAll(collections);
         result.addAll(sets);
         result.addAll(sources);
         result.addAll(streams);

         return result
            .stream()
            .sorted(Comparator.comparing(DataAssetProperties::getName))
            .collect(Collectors.toList());
      });
   }

   @Override
   public CompletionStage<List<ProjectProperties>> getUserProjects(User executor, String userId) {
      return getProjects(AuthenticatedUser.apply(userId));
   }

   @Override
   public CompletionStage<List<DataAssetProperties<?>>> getUserDataAssets(User executor, String userId) {
      return getDataAssets(AuthenticatedUser.apply(userId));
   }

}
