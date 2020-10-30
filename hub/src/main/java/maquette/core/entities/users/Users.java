package maquette.core.entities.users;

import lombok.AllArgsConstructor;
import maquette.core.ports.UsersRepository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class Users {

   private final UsersRepository repository;

   public CompletionStage<User> findUserById(String id) {
      return CompletableFuture.completedFuture(User.apply(id, repository));
   }

}
