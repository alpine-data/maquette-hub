package maquette.core.entities.users;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.core.entities.users.exceptions.UserNotFoundException;
import maquette.core.entities.users.model.UserDetails;
import maquette.core.entities.users.model.UserNotification;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.entities.users.model.UserSettings;
import maquette.core.ports.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class UserEntity {

   private static final String SECRET_MASK = "__secret__";

   private static final Logger LOG = LoggerFactory.getLogger(UserEntity.class);

   private final String id;

   private final UsersRepository repository;

   private final ObjectMapper om;

   public CompletionStage<Done> createNewNotification(String message) {
      var notification = UserNotification.apply(UUID.randomUUID().toString(), Instant.now(), false, message);
      return repository.insertOrUpdateNotification(id, notification);
   }

   public CompletionStage<Done> updateUserDetails(String base64encodedUserDetails) {
      try {
         var json = new String(Base64.getDecoder().decode(base64encodedUserDetails), StandardCharsets.UTF_8);
         var update = om.readValue(json, UserDetails.class);

         return repository
            .findProfileById(id)
            .thenApply(profile -> profile.orElse(UserProfile.apply(id)))
            .thenApply(profile -> profile
               .withEmail(update.getEmail())
               .withName(update.getName()))
            .thenCompose(repository::insertOrUpdateProfile);
      } catch (IOException ex) {
         LOG.warn("An error occurred while updating user details for user `{}`", id, ex);
      }

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   public CompletionStage<Done> updateUserProfile(UserProfile profile) {
      return repository.insertOrUpdateProfile(profile.withId(id));
   }

   public CompletionStage<Done> updateUserSettings(UserSettings settings) {
      return repository
         .findSettingsById(id)
         .thenApply(maybeSettings -> {
            if (maybeSettings.isEmpty()) {
               return UserSettings.apply();
            } else {
               return maybeSettings.get();
            }
         })
         .thenApply(current -> {
            var updatedSettings = settings;

            if (settings.getGit().getPassword().equals(SECRET_MASK)) {
               updatedSettings = updatedSettings
                  .withGit(updatedSettings.getGit().withPassword(current.getGit().getPassword()));
            }

            if (settings.getGit().getPrivateKey().equals(SECRET_MASK)) {
               updatedSettings = updatedSettings
                  .withGit(updatedSettings.getGit().withPrivateKey(current.getGit().getPrivateKey()));
            }

            if (settings.getGit().getPublicKey().equals(SECRET_MASK)) {
               updatedSettings = updatedSettings
                  .withGit(updatedSettings.getGit().withPublicKey(current.getGit().getPublicKey()));
            }

            return updatedSettings;
         })
         .thenCompose(updatedSettings -> repository.insertOrUpdateSettings(id, updatedSettings));
   }

   public CompletionStage<UserProfile> getProfile() {
      return repository
         .findProfileById(id)
         .thenApply(profile -> profile.orElse(UserProfile.apply(id)));
   }

   public CompletionStage<UserSettings> getSettings() {
      return getSettings(true);
   }

   public CompletionStage<UserSettings> getSettings(boolean masked) {
      return repository
         .findSettingsById(id)
         .thenApply(settings -> settings.orElse(UserSettings.apply()))
         .thenApply(settings -> {
            if (masked) {
               return settings.withGit(settings.getGit()
                  .withPassword(SECRET_MASK)
                  .withPrivateKey(SECRET_MASK)
                  .withPublicKey(SECRET_MASK));
            } else {
               return settings;
            }
         });
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
