package maquette.core.modules.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.core.modules.ports.UsersRepository;
import maquette.core.modules.users.exceptions.InvalidAuthenticationTokenException;
import maquette.core.modules.users.model.UserProfile;
import maquette.core.values.UID;
import maquette.core.values.user.AuthenticatedUser;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class UserEntities {

    private final UsersRepository repository;

    private final ObjectMapper objectMapper;

    public CompletionStage<UserEntity> getUserById(UID id) {
        return CompletableFuture.completedFuture(UserEntity.apply(id, repository, objectMapper));
    }

    public CompletionStage<List<UserProfile>> getUsers(String query) {

        if (StringUtils.isEmpty(query)) {
            return repository.getUsers();
        } else {
            return repository.getUsers(query);
        }
    }

    public CompletionStage<AuthenticatedUser> getUserForAuthenticationToken(UID tokenId, String tokenSecret) {
        return repository
            .findAuthenticationTokenByTokenId(tokenId)
            .thenCompose(maybeToken -> {
                if (maybeToken.isPresent() && maybeToken.get().getValidBefore().isAfter(Instant.now())) {
                    return repository
                        .findProfileByAuthenticationToken(maybeToken.get().getId())
                        .thenCompose(maybeProfile -> maybeProfile
                            .map(userProfile -> CompletableFuture.completedFuture(AuthenticatedUser.apply(userProfile.getId())))
                            .orElseGet(() -> CompletableFuture.failedFuture(InvalidAuthenticationTokenException.createUnknownToken(tokenId
                                .getValue()))));
                } else if (maybeToken.isPresent()) {
                    return CompletableFuture.failedFuture(InvalidAuthenticationTokenException.createOutdated(tokenId.getValue()));
                } else {
                    return CompletableFuture.failedFuture(InvalidAuthenticationTokenException.createUnknownToken(tokenId
                        .getValue()));
                }
            });
    }

}
