package maquette.core.entities.users;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.users.model.UserNotification;
import maquette.core.ports.UsersRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class User {

   private final String id;

   private final UsersRepository repository;

   public CompletionStage<Done> createNewNotification(String message) {
      var notification = UserNotification.apply(UUID.randomUUID().toString(), Instant.now(), false, message);
      return repository.insertOrUpdateNotification(id, notification);
   }

   public CompletionStage<List<UserNotification>> getNotifications() {
      return repository.getAllNotifications(id);
   }

   public CompletionStage<Done> readNotification(String notificationId) {
      return repository
         .findNotificationById(id, notificationId)
         .thenCompose(maybeNotification -> {
            if (maybeNotification.isPresent()) {
               var notification = maybeNotification.get().withRead(true);
               return repository.insertOrUpdateNotification(id, notification);
            } else {
               return CompletableFuture.completedFuture(Done.getInstance());
            }
         });
   }

}
