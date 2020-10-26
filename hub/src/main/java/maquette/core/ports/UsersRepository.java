package maquette.core.ports;

import akka.Done;
import maquette.core.entities.users.model.UserNotification;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface UsersRepository {

   CompletionStage<Done> insertOrUpdateNotification(String userId, UserNotification notification);

   CompletionStage<Optional<UserNotification>> findNotificationById(String userId, String notificationId);

   CompletionStage<List<UserNotification>> getAllNotifications(String userId);

}
