package maquette.core.ports;

import akka.Done;
import maquette.core.entities.users.model.UserNotification;
import maquette.core.entities.users.model.UserProfile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface UsersRepository {

   CompletionStage<Done> insertOrUpdateProfile(UserProfile profile);

   CompletionStage<Done> insertOrUpdateNotification(String userId, UserNotification notification);

   CompletionStage<Optional<UserNotification>> findNotificationById(String userId, String notificationId);

   CompletionStage<Optional<UserProfile>> findProfileById(String userId);

   CompletionStage<List<UserNotification>> getAllNotifications(String userId);

}
