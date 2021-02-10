package maquette.core.entities.users;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.core.entities.users.model.UserDetails;
import maquette.core.entities.users.model.UserNotification;
import maquette.core.entities.users.model.UserProfile;
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

   public CompletionStage<UserProfile> getProfile() {
      return repository
         .findProfileById(id)
         .thenApply(profile -> profile.orElse(UserProfile.apply(id)));
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
