package maquette.core.entities.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.ports.UsersRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class UserEntities {

   private final UsersRepository repository;

   private final ObjectMapper objectMapper;

   public CompletionStage<UserEntity> findUserById(String id) {
      return CompletableFuture.completedFuture(UserEntity.apply(id, repository, objectMapper));
   }

   public CompletionStage<List<UserProfile>> getUsers() {
      return repository.getUsers();
   }

}
