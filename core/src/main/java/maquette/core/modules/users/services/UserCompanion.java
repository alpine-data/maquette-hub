package maquette.core.modules.users.services;

import lombok.AllArgsConstructor;
import maquette.core.modules.ServicesCompanion;
import maquette.core.modules.users.UserEntities;
import maquette.core.modules.users.UserEntity;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@AllArgsConstructor(staticName = "apply")
public final class UserCompanion extends ServicesCompanion {

   private final UserEntities users;

   public CompletionStage<UserEntity> withUser(User user) {
      if (user instanceof AuthenticatedUser) {
         return users.getUserById(((AuthenticatedUser) user).getId());
      } else {
         return CompletableFuture.failedFuture(new RuntimeException("Not an authenticated user")); // TODO mw: Better exception?
      }
   }

   public CompletionStage<UserEntity> withUser(String userId) {
      return users.getUserById(userId);
   }

   public <T> CompletionStage<T> withUserOrDefault(User user, T defaultValue, Function<UserEntity, CompletionStage<T>> action) {
      if (user instanceof AuthenticatedUser) {
         return users.getUserById(((AuthenticatedUser) user).getId()).thenCompose(action);
      } else {
         return CompletableFuture.completedFuture(defaultValue);
      }
   }

}
