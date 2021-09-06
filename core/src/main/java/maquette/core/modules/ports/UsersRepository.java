package maquette.core.modules.ports;

import akka.Done;
import maquette.core.modules.users.model.UserAuthenticationToken;
import maquette.core.modules.users.model.UserNotification;
import maquette.core.modules.users.model.UserProfile;
import maquette.core.modules.users.model.UserSettings;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface UsersRepository {

   CompletionStage<Done> insertOrUpdateProfile(UserProfile profile);

   CompletionStage<Done> insertOrUpdateNotification(String userId, UserNotification notification);

   CompletionStage<Done> insertOrUpdateSettings(String userId, UserSettings settings);

   CompletionStage<Done> insertOrUpdateAuthenticationToken(String userId, UserAuthenticationToken token);

   CompletionStage<List<UserProfile>> getUsers();

   CompletionStage<Optional<UserAuthenticationToken>> findAuthenticationTokenByUserId(String userId);

   CompletionStage<Optional<UserAuthenticationToken>> findAuthenticationTokenByTokenId(String tokenId);

   CompletionStage<Optional<UserNotification>> findNotificationById(String userId, String notificationId);

   CompletionStage<Optional<UserProfile>> findProfileById(String userId);

   CompletionStage<Optional<UserSettings>> findSettingsById(String userId);

   CompletionStage<List<UserNotification>> getAllNotifications(String userId);

}
