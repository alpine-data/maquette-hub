package maquette.core.modules.ports;

import akka.Done;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.common.exceptions.ApplicationException;
import maquette.core.modules.users.model.UserAuthenticationToken;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryAuthenticationTokenStore implements AuthenticationTokenStore {

    private final Map<String, UserAuthenticationToken> store;

    public static InMemoryAuthenticationTokenStore apply() {
        return apply(Maps.newHashMap());
    }

    @Override
    public CompletionStage<Done> put(String randomId, UserAuthenticationToken token) {
        store.put(randomId, token);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<UserAuthenticationToken> get(String randomId) {
        if (store.containsKey(randomId)) {
            var value = store.remove(randomId);
            return CompletableFuture.completedFuture(value);
        } else {
            return CompletableFuture.failedFuture(TokenNotFoundException.apply(randomId));
        }
    }

    public static class TokenNotFoundException extends ApplicationException {

        private TokenNotFoundException(String message) {
            super(message);
        }

        public static TokenNotFoundException apply(String randomId) {
            String message = String.format("No token registered for id `%s`.", randomId);
            return new TokenNotFoundException(message);
        }

    }
}
