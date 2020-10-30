package maquette.core.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.users.Users;
import maquette.core.entities.users.model.UserNotification;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class UserServicesImpl implements UserServices {

   private final Users users;

   @Override
   public CompletionStage<List<UserNotification>> getNotifications(User executor) {
      if (executor instanceof AuthenticatedUser) {
         var userId = ((AuthenticatedUser) executor).getId();

         return users
            .findUserById(userId)
            .thenCompose(maquette.core.entities.users.User::getNotifications);
      } else {
         return CompletableFuture.completedFuture(Lists.newArrayList());
      }
   }

   @Override
   public CompletionStage<Done> readNotification(User executor, String notificationId) {
      if (executor instanceof AuthenticatedUser) {
         var userId = ((AuthenticatedUser) executor).getId();

         return users
            .findUserById(userId)
            .thenCompose(user -> user.readNotification(notificationId));
      } else {
         return CompletableFuture.completedFuture(Done.getInstance());
      }
   }

}
