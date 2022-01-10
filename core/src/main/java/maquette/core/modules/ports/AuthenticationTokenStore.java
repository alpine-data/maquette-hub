package maquette.core.modules.ports;

import akka.Done;
import maquette.core.modules.users.model.UserAuthenticationToken;

import java.util.concurrent.CompletionStage;

public interface AuthenticationTokenStore {

    CompletionStage<Done> put(String randomId, UserAuthenticationToken token);

    CompletionStage<UserAuthenticationToken> get(String randomId);

}
