package maquette.adapters.users;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.users.model.UserNotification;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.entities.users.model.UserSettings;
import maquette.core.ports.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

   private static final Logger LOG = LoggerFactory.getLogger(FileSystemUsersRepository.class);

   private final FileSystemUsersRepositoryConfiguration config;

   private final ObjectMapper om;

   private Path getUserDirectory(String userId) {
      var dir = config.getDirectory().resolve(userId);
      Operators.suppressExceptions(() -> Files.createDirectories(dir));
      return dir;
   }

   private Path getUserProfileFile(String userId) {
      return getUserDirectory(userId).resolve("profile.json");
   }

   private Path getUserSettingsFile(String userId) {
      return getUserDirectory(userId).resolve("settings.json");
   }

   private Path getNotificationDirectory(String userId) {
      return getUserDirectory(userId).resolve("notifications");
   }

   private Path getNotificationFile(String userId, String notificationId) {
      return getNotificationDirectory(userId).resolve(notificationId);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateProfile(UserProfile profile) {
      var file = getUserProfileFile(profile.getId());
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), profile));
      return CompletableFuture.completedFuture(Done.getInstance());
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
   public CompletionStage<Done> insertOrUpdateSettings(String userId, UserSettings settings) {
      var file = getUserSettingsFile(userId);

      Operators.suppressExceptions(() -> {
         try (OutputStream os = Files.newOutputStream(file)) {
            om.writeValue(os, settings);
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
   public CompletionStage<Optional<UserProfile>> findProfileById(String userId) {
      var file = getUserProfileFile(userId);

      if (Files.exists(file)) {
         return Operators.ignoreExceptionsWithDefault(
            () -> {
               var result = om.readValue(file.toFile(), UserProfile.class);
               return CompletableFuture.completedFuture(Optional.of(result));
            },
            CompletableFuture.completedFuture(Optional.empty()), LOG);
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

   @Override
   public CompletionStage<Optional<UserSettings>> findSettingsById(String userId) {
      var file = getUserSettingsFile(userId);

      if (Files.exists(file)) {
         return Operators.ignoreExceptionsWithDefault(
            () -> {
               var result = om.readValue(file.toFile(), UserSettings.class);
               return CompletableFuture.completedFuture(Optional.of(result));
            },
            CompletableFuture.completedFuture(Optional.empty()));
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
