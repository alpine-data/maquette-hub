package maquette.core.modules.users.services;

import lombok.AllArgsConstructor;
import maquette.core.modules.ServicesCompanion;
import maquette.core.modules.users.UserEntities;
import maquette.core.modules.users.UserEntity;
import maquette.core.modules.users.exceptions.NoAuthenticatedUserException;
import maquette.core.values.UID;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@AllArgsConstructor(staticName = "apply")
public final class UserCompanion extends ServicesCompanion {

    private final UserEntities users;

    /**
     * Resolves the request's user to the user entity to access more specific user information.
     * Call this function only if it's sure that the user is an authenticated (personal) user.
     *
     * @param user The request's executor.
     * @return The resolved user entity.
     * @throws NoAuthenticatedUserException If the user is not an authenticated user (e.g. technical user, anonymous user).
     */
    public CompletionStage<UserEntity> withUser(User user) {
        if (user instanceof AuthenticatedUser) {
            return users.getUserById(((AuthenticatedUser) user).getId());
        } else {
            return CompletableFuture.failedFuture(NoAuthenticatedUserException.apply(user));
        }
    }

    /**
     * Resolves the unique user ID to a user entity.
     *
     * @param userId The unique user id.
     * @return The resolved user entity.
     */
    public CompletionStage<UserEntity> withUser(UID userId) {
        return users.getUserById(userId);
    }

    /**
     * Use this function to proceed with a resolved user entity. Specify an alternative result,
     * if user is not a valid personal/ authenticated user.
     *
     * @param user         The current executor of the action.
     * @param defaultValue The default value which should be returned if user is not an authenticated personal user.
     * @param action       The function to be executed, if user is a valid user.
     * @param <T>          Type of the return value.
     * @return The default value or result of the action.
     */
    public <T> CompletionStage<T> withUserOrDefault(User user, T defaultValue, Function<UserEntity,
        CompletionStage<T>> action) {
        if (user instanceof AuthenticatedUser) {
            return users
                .getUserById(((AuthenticatedUser) user).getId())
                .thenCompose(action);
        } else {
            return CompletableFuture.completedFuture(defaultValue);
        }
    }

}
