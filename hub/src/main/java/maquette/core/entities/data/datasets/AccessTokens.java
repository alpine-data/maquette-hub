package maquette.core.entities.data.datasets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.ports.DatasetsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.access.DataAccessToken;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class AccessTokens {

   private final String id;

   private final String projectId;

   private final String fullId;

   private final String name;

   private final DatasetsRepository repository;

   public CompletionStage<DataAccessToken> createDataAccessToken(User executor, String origin, String name, String description) {
      var created = ActionMetadata.apply(executor);
      var key = UUID.randomUUID().toString();
      var secret = UUID.randomUUID().toString();
      var token = DataAccessToken.apply(created, name, description, key, secret, origin);

      return repository
         .insertDataAccessToken(getFullId(), token)
         .thenApply(done -> token);
   }

   public CompletionStage<List<DataAccessToken>> getDataAccessTokens() {
      return repository.findDataAccessTokensByParent(getFullId());
   }

   public CompletionStage<Optional<DataAccessToken>> getDataAccessTokenById(String accessTokenKey) {
      return repository.findDataAccessTokenByKey(getFullId(), accessTokenKey);
   }

}
