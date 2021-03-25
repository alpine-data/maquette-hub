package maquette.core.services.users;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.users.UserEntities;
import maquette.core.entities.users.UserEntity;
import maquette.core.entities.users.model.UserNotification;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.entities.users.model.UserSettings;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import org.apache.commons.compress.utils.Lists;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class UserServicesImpl implements UserServices {

   private final DataAssetEntities assets;

   private final ProjectEntities projects;

   private final UserEntities users;

   private final UserCompanion companion;

   private final DataAssetCompanion assetCompanion;

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
   public CompletionStage<UserProfile> getProfile(User executor) {
      return companion
         .withUser(executor)
         .thenCompose(UserEntity::getProfile);
   }

   @Override
   public CompletionStage<UserSettings> getSettings(User executor, String userId) {
      return companion
         .withUser(userId)
         .thenCompose(entity -> entity.getSettings(true));
   }

   @Override
   public CompletionStage<List<UserProfile>> getUsers(User executor) {
      return users.getUsers();
   }

   @Override
   public CompletionStage<Done> updateUserDetails(User executor, String base64encodedDetails) {
      return companion
         .withUser(executor)
         .thenCompose(entity -> entity.updateUserDetails(base64encodedDetails));
   }

   @Override
   public CompletionStage<Done> updateUser(User executor, String userId, UserProfile profile, UserSettings settings) {
      return companion
         .withUser(userId)
         .thenCompose(entity -> entity.updateUserProfile(profile).thenApply(d -> entity))
         .thenCompose(entity -> entity.updateUserSettings(settings));
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
   public CompletionStage<List<DataAssetProperties>> getDataAssets(User user) {
      return assets
         .list()
         .thenApply(assets -> assets
            .stream()
            .map(p -> assetCompanion.filterMember(user, p.getMetadata().getName(), p)))
         .thenCompose(Operators::allOf)
         .thenApply(Operators::filterOptional)
         .thenApply(assets -> assets
            .stream()
            .sorted(Comparator.comparing(p -> p.getMetadata().getName()))
            .collect(Collectors.toList()));
   }

   @Override
   public CompletionStage<List<ProjectProperties>> getUserProjects(User executor, String userId) {
      return getProjects(AuthenticatedUser.apply(userId));
   }

   @Override
   public CompletionStage<List<DataAssetProperties>> getUserDataAssets(User executor, String userId) {
      return getDataAssets(AuthenticatedUser.apply(userId));
   }

}
