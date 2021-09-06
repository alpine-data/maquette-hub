package maquette.core.modules.users.services;

import akka.Done;
import maquette.core.modules.users.model.UserAuthenticationToken;
import maquette.core.modules.users.model.UserNotification;
import maquette.core.modules.users.model.UserProfile;
import maquette.core.modules.users.model.UserSettings;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface UserServices {

   /*
    * Authentication tokens
    */
   CompletionStage<UserAuthenticationToken> getAuthenticationToken(User executor);

   CompletionStage<Optional<AuthenticatedUser>> getUserForAuthenticationToken(String tokenId, String tokenSecret);

   /*
    * Profile
    */
   CompletionStage<UserProfile> getProfile(User executor, String userId);

   CompletionStage<UserProfile> getProfile(User executor);

   CompletionStage<UserSettings> getSettings(User executor, String userId);

   CompletionStage<List<UserProfile>> getUsers(User executor);

   CompletionStage<Done> updateUserDetails(User executor, String base64encodedDetails);

   CompletionStage<Done> updateUser(User executor, String userId, UserProfile profile, UserSettings settings);

   /*
    * Notifications
    */
   CompletionStage<List<UserNotification>> getNotifications(User executor);

   CompletionStage<Done> readNotification(User executor, String notificationId);

}
