package maquette.core.modules.users;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import maquette.core.common.exceptions.ApplicationException;
import maquette.core.modules.ports.AuthenticationTokenStore;
import maquette.core.modules.ports.UsersRepository;
import maquette.core.modules.users.exceptions.InvalidAuthenticationTokenException;
import maquette.core.modules.users.model.UserAuthenticationToken;
import maquette.core.modules.users.model.UserProfile;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class UserEntities {

    private final UsersRepository repository;

    private final AuthenticationTokenStore tokens;

    private final ObjectMapper objectMapper;

    public CompletionStage<Set<GlobalRole>> getGlobalRolesForUser(User executor) {
        if (executor instanceof AuthenticatedUser) {
            return repository
                .getAllGlobalAuthorizations()
                .thenApply(roles -> roles
                    .stream()
                    .filter(auth -> auth.getAuthorization().authorizes(executor))
                    .map(GrantedAuthorization::getRole)
                    .collect(Collectors.toSet()));
        } else {
            return CompletableFuture.completedFuture(Sets.newHashSet());
        }
    }

    public CompletionStage<List<GrantedAuthorization<GlobalRole>>> getGlobalRoles() {
        return repository.getAllGlobalAuthorizations();
    }

    public CompletionStage<Boolean> hasGlobalRole(User executor, GlobalRole role) {
        return getGlobalRolesForUser(executor).thenApply(roles -> roles.contains(role));
    }

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
                if (maybeToken.isPresent() && maybeToken
                    .get()
                    .getValidBefore()
                    .isAfter(Instant.now())) {
                    return repository
                        .findProfileByAuthenticationToken(maybeToken
                            .get()
                            .getId())
                        .thenCompose(maybeProfile -> maybeProfile
                            .map(userProfile -> CompletableFuture.completedFuture(
                                AuthenticatedUser.apply(userProfile.getId())))
                            .orElseGet(() -> CompletableFuture.failedFuture(
                                InvalidAuthenticationTokenException.createUnknownToken(tokenId
                                    .getValue()))));
                } else if (maybeToken.isPresent()) {
                    return CompletableFuture.failedFuture(
                        InvalidAuthenticationTokenException.createOutdated(tokenId.getValue()));
                } else {
                    return CompletableFuture.failedFuture(InvalidAuthenticationTokenException.createUnknownToken(tokenId
                        .getValue()));
                }
            });
    }

    public CompletionStage<UserAuthenticationToken> readAuthenticationToken(String randomId) {
        return tokens.get(randomId);
    }

    public CompletionStage<Done> registerAuthenticationToken(User executor, String randomId) {
        if (executor instanceof AuthenticatedUser) {
            var id = ((AuthenticatedUser) executor).getId();
            return getUserById(id).thenCompose(user -> user
                .getAuthenticationToken()
                .thenCompose(token -> tokens.put(randomId, token)));
        } else {
            return CompletableFuture.failedFuture(UserNotAuthenticatedException.apply());
        }
    }

    public CompletionStage<Done> grantGlobalRole(User executor, Authorization authorization, GlobalRole role) {
        return this.repository.insertGlobalAuthorization(GrantedAuthorization.apply(
            ActionMetadata.apply(executor),
            authorization,
            role));
    }

    public CompletionStage<Done> removeGlobalRole(User executor, Authorization authorization, GlobalRole role) {
        return this.repository.removeGlobalAuthorization(authorization, role);
    }

    public static class UserNotAuthenticatedException extends ApplicationException {

        private UserNotAuthenticatedException(String message) {
            super(message);
        }

        public static UserNotAuthenticatedException apply() {
            String message = "User is not authenticated, but an authenticated user is required to execute this action.";
            return new UserNotAuthenticatedException(message);
        }

    }

}
