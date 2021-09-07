package maquette.core.modules.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.core.modules.ports.UsersRepository;
import maquette.core.modules.users.model.UserProfile;
import maquette.core.values.user.AuthenticatedUser;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class UserEntities {

   private final UsersRepository repository;

   private final ObjectMapper objectMapper;

   public CompletionStage<UserEntity> getUserById(String id) {
      return CompletableFuture.completedFuture(UserEntity.apply(id, repository, objectMapper));
   }

   public CompletionStage<List<UserProfile>> getUsers() {
      return repository.getUsers();
   }

   public CompletionStage<Optional<AuthenticatedUser>> getUserForAuthenticationToken(String tokenId, String tokenSecret) {
      return repository
         .findAuthenticationTokenByTokenId(tokenId)
         .thenCompose(maybeToken -> {
            if (maybeToken.isPresent() && maybeToken.get().getValidBefore().isBefore(Instant.now())) {
               return repository
                  .findProfileById(tokenId)
                  .thenApply(maybeProfile -> maybeProfile.map(userProfile -> AuthenticatedUser.apply(userProfile.getId())));
            } else {
               return CompletableFuture.completedFuture(Optional.empty());
            }
         });
   }

}
