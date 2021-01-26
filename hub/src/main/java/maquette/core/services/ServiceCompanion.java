package maquette.core.services;

import akka.Done;
import maquette.core.values.exceptions.NotAuthorizedException;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

public class ServiceCompanion {

   @SafeVarargs
   public final <T> CompletionStage<Optional<T>> filterAuthorized(T passThrough, Supplier<CompletionStage<Boolean>>... checks) {
      return filterAuthorized(passThrough, Arrays.asList(checks));
   }

   public final <T> CompletionStage<Optional<T>> filterAuthorized(T passThrough, List<Supplier<CompletionStage<Boolean>>> checks) {
      if (checks.isEmpty()) {
         return CompletableFuture.completedFuture(Optional.empty());
      } else {
         return checks.get(0).get().thenCompose(r -> {
            if (r) {
               return CompletableFuture.completedFuture(Optional.of(passThrough));
            } else {
               return filterAuthorized(passThrough, checks.subList(1, checks.size()));
            }
         });
      }
   }

   public final <T> CompletionStage<Optional<T>> filterAuthenticatedUser(T passThrough, User user) {
      if (user instanceof AuthenticatedUser) {
         return CompletableFuture.completedFuture(Optional.of(passThrough));
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

   @SafeVarargs
   public final CompletionStage<Boolean> isAuthorized(Supplier<CompletionStage<Boolean>>... checks) {
      return filterAuthorized(Done.getInstance(), checks).thenApply(Optional::isPresent);
   }

   public final CompletionStage<Boolean> isAuthenticatedUser(User user) {
      return filterAuthenticatedUser(Done.getInstance(), user).thenApply(Optional::isPresent);
   }

   @SafeVarargs
   public final CompletionStage<Done> withAuthorization(Supplier<CompletionStage<Boolean>>... checks) {
      return isAuthorized(checks).thenCompose(authorized -> {
         if (authorized) {
            return CompletableFuture.completedFuture(Done.getInstance());
         } else {
            return CompletableFuture.failedFuture(NotAuthorizedException.apply("You are not authorized to execute this action."));
         }
      });
   }

}
