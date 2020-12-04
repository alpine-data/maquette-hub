package maquette.core.entities.data.datasets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.ports.common.HasDataAccessTokens;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessToken;
import maquette.core.values.user.User;
import org.apache.commons.lang.NotImplementedException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class AccessTokens {

   private final UID id;

   private final HasDataAccessTokens repository;

   public CompletionStage<DataAccessToken> createDataAccessToken(User executor, String origin, String name, String description) {
      /*
      var created = ActionMetadata.apply(executor);
      var key = UUID.randomUUID().toString();
      var secret = UUID.randomUUID().toString();
      var token = DataAccessToken.apply(created, name, description, key, secret, origin);

      return repository
         .insertDataAccessToken(getFullId(), token)
         .thenApply(done -> token);
    */
      throw new NotImplementedException();
   }

   public CompletionStage<List<DataAccessToken>> getDataAccessTokens() {
      throw new NotImplementedException();
   }

   public CompletionStage<Optional<DataAccessToken>> getDataAccessTokenById(String accessTokenKey) {
      throw new NotImplementedException();
   }

}
