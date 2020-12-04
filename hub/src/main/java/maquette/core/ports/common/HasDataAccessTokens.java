package maquette.core.ports.common;

import akka.Done;
import maquette.core.values.access.DataAccessToken;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface HasDataAccessTokens {

   CompletionStage<Done> insertDataAccessToken(String parentId, DataAccessToken token);

   CompletionStage<Optional<DataAccessToken>> findDataAccessTokenByKey(String parentId, String key);

   CompletionStage<List<DataAccessToken>> findDataAccessTokensByParent(String parentId);

   CompletionStage<Done> removeDataAccessToken(String parentId, String key);

}
