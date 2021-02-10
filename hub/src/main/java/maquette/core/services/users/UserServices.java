package maquette.core.services.users;

import akka.Done;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.users.model.UserNotification;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface UserServices {

   /*
    * Profile
    */
   CompletionStage<UserProfile> getProfile(User executor, String userId);

   CompletionStage<Done> updateUserDetails(User executor, String base64encodedDetails);

   /*
    * Notifications
    */

   CompletionStage<List<UserNotification>> getNotifications(User executor);

   CompletionStage<Done> readNotification(User executor, String notificationId);

   /*
    * Assets
    */
   CompletionStage<List<ProjectProperties>> getProjects(User user);

   CompletionStage<List<DataAssetProperties<?>>> getDataAssets(User user);

   CompletionStage<List<ProjectProperties>> getUserProjects(User executor, String userId);

   CompletionStage<List<DataAssetProperties<?>>> getUserDataAssets(User executor, String userId);

}
