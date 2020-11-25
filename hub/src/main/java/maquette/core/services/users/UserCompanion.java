package maquette.core.services.users;

import lombok.AllArgsConstructor;
import maquette.core.entities.users.Users;
import maquette.core.services.ServiceCompanion;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@AllArgsConstructor(staticName = "apply")
public final class UserCompanion extends ServiceCompanion  {

   private final Users users;

   public CompletionStage<maquette.core.entities.users.User> withUser(User user) {
      if (user instanceof AuthenticatedUser) {
         return users.findUserById(((AuthenticatedUser) user).getId());
      } else {
         return CompletableFuture.failedFuture(new RuntimeException("Not an authenticated user")); // TODO mw: Better exception?
      }
   }

   public <T> CompletionStage<T> withUserOrDefault(User user, T defaultValue, Function<maquette.core.entities.users.User, CompletionStage<T>> action) {
      if (user instanceof AuthenticatedUser) {
         return users.findUserById(((AuthenticatedUser) user).getId()).thenCompose(action);
      } else {
         return CompletableFuture.completedFuture(defaultValue);
      }
   }

}
