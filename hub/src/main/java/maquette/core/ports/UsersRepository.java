package maquette.core.ports;

import akka.Done;
import maquette.core.entities.users.model.UserNotification;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.entities.users.model.UserSettings;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface UsersRepository {

   CompletionStage<Done> insertOrUpdateProfile(UserProfile profile);

   CompletionStage<Done> insertOrUpdateNotification(String userId, UserNotification notification);

   CompletionStage<Done> insertOrUpdateSettings(String userId, UserSettings settings);

   CompletionStage<List<UserProfile>> getUsers();

   CompletionStage<Optional<UserNotification>> findNotificationById(String userId, String notificationId);

   CompletionStage<Optional<UserProfile>> findProfileById(String userId);

   CompletionStage<Optional<UserSettings>> findSettingsById(String userId);

   CompletionStage<List<UserNotification>> getAllNotifications(String userId);

}
