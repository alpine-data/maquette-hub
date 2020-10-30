package maquette.adapters.users;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.users.model.UserNotification;
import maquette.core.ports.UsersRepository;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class FileSystemUsersRepository implements UsersRepository {

   private final FileSystemUsersRepositoryConfiguration config;

   private final ObjectMapper om;

   private Path getNotificationDirectory(String userId) {
      return config.getDirectory().resolve(userId).resolve("notifications");
   }

   private Path getNotificationFile(String userId, String notificationId) {
      return getNotificationDirectory(userId).resolve(notificationId);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateNotification(String userId, UserNotification notification) {
      var file = getNotificationFile(userId, notification.getId());

      Operators.suppressExceptions(() -> {
         try (OutputStream os = Files.newOutputStream(file)) {
            om.writeValue(os, notification);
         }
      });

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Optional<UserNotification>> findNotificationById(String userId, String notificationId) {
      var file = getNotificationFile(userId, notificationId);

      if (Files.exists(file)) {
         var result = Operators.suppressExceptions(() -> om.readValue(file.toFile(), UserNotification.class));
         return CompletableFuture.completedFuture(Optional.of(result));
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

   @Override
   public CompletionStage<List<UserNotification>> getAllNotifications(String userId) {
      var result = Operators.suppressExceptions(() -> Files
         .list(getNotificationDirectory(userId))
         .filter(Files::isRegularFile)
         .map(file -> Operators.ignoreExceptionsWithDefault(
            () -> Optional.of(om.readValue(file.toFile(), UserNotification.class)),
            Optional.<UserNotification>empty())))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }
}
