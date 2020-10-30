package maquette.core.ports;

import akka.Done;
import maquette.core.values.authorization.UserAuthorization;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface HasDataOwner {

   CompletionStage<List<UserAuthorization>> findAllOwners(String parentId);

   CompletionStage<Done> insertOwner(String parentId, UserAuthorization owner);

   CompletionStage<Done> removeOwner(String parentId, String userId);

}
