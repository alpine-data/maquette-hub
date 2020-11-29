package maquette.adapters.users;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.users.model.UserNotification;
import maquette.core.ports.UsersRepository;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryUsersRepository implements UsersRepository {

   private final List<StoredUserNotification> notifications;

   public static InMemoryUsersRepository apply() {
      return apply(Lists.newArrayList());
   }

   @Override
   public CompletionStage<Done> insertOrUpdateNotification(String userId, UserNotification notification) {
      notifications
         .stream()
         .filter(n -> n.userId.equals(userId) && n.notification.getId().equals(userId))
         .forEach(notifications::remove);

      notifications.add(StoredUserNotification.apply(userId, notification));

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Optional<UserNotification>> findNotificationById(String userId, String notificationId) {
      var result = notifications
         .stream()
         .filter(notification -> notification.userId.equals(userId) && notification.notification.getId().equals(notificationId))
         .map(StoredUserNotification::getNotification)
         .findAny();

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<List<UserNotification>> getAllNotifications(String userId) {
      var result = notifications
         .stream()
         .filter(n -> n.userId.equals(userId))
         .map(StoredUserNotification::getNotification)
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   private static class StoredUserNotification {

      String userId;

      UserNotification notification;

   }

}
