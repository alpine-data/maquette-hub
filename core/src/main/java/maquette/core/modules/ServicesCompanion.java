package maquette.core.modules;

import akka.Done;
import maquette.core.common.exceptions.NotAuthorizedException;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

/**
 * A helper companion class to which includes common methods used by service implementation.s
 */
public class ServicesCompanion {

    /**
     * Chains a list of authorization checks. If one check is successful, the passThrough will be returned, otherwise
     * an empty value will be returned.
     *
     * @param passThrough The passThrough value.
     * @param checks      The list of checks to execute.
     * @param <T>         The type of the passThrough
     * @return The passThrough value or empty, of no check is successful.
     */
    @SafeVarargs
    public final <T> CompletionStage<Optional<T>> filterAuthorized(T passThrough,
                                                                   Supplier<CompletionStage<Boolean>>... checks) {
        return filterAuthorized(passThrough, Arrays.asList(checks));
    }

    /**
     * Chains a list of authorization checks. If one check is successful, the passThrough will be returned, otherwise
     * an empty value will be returned.
     *
     * @param passThrough The passThrough value.
     * @param checks      The list of checks to execute.
     * @param <T>         The type of the passThrough
     * @return The passThrough value or empty, of no check is successful.
     */
    public final <T> CompletionStage<Optional<T>> filterAuthorized(T passThrough,
                                                                   List<Supplier<CompletionStage<Boolean>>> checks) {
        if (checks.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.empty());
        } else {
            return checks.get(0).get().thenCompose(r -> {
                if (r) {
                    return CompletableFuture.completedFuture(Optional.of(passThrough));
                } else {
                    return filterAuthorized(passThrough, checks.subList(1, checks.size()));
                }
            });
        }
    }

    /**
     * This method checks whether a user is authenticated. The passThrough will be returned as the result of the
     * future if the user is authenticated. If not, an empty result will be returned.
     *
     * @param passThrough The passThrough value.
     * @param user        The user to check for authentication.
     * @param <T>         The type of the passThrough.
     * @return The passThrough value or empty.
     */
    public final <T> CompletionStage<Optional<T>> filterAuthenticatedUser(T passThrough, User user) {
        if (user instanceof AuthenticatedUser) {
            return CompletableFuture.completedFuture(Optional.of(passThrough));
        } else {
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }

    /**
     * Helper function to chain check-methods to ensure user authorization. If all checks fail, False
     * will be returned. A single successful check will return True.
     *
     * @param checks The list of checks to execute.
     * @return True if all checks have been successful, otherwise False.
     */
    @SafeVarargs
    public final CompletionStage<Boolean> isAuthorized(Supplier<CompletionStage<Boolean>>... checks) {
        return filterAuthorized(Done.getInstance(), checks).thenApply(Optional::isPresent);
    }

    /**
     * Checks whether the provided user is authenticated.
     *
     * @param user The user to check.
     * @return Returns true if the user authenticated, otherwise false.
     */
    public final CompletionStage<Boolean> isAuthenticatedUser(User user) {
        return filterAuthenticatedUser(Done.getInstance(), user).thenApply(Optional::isPresent);
    }

    /**
     * Check whether the user is a super user with special authorizations.
     *
     * @param user The user to check.
     * @return Returns true, if the user is a super user of the system.
     */
    public final CompletionStage<Boolean> isSuperUser(User user) {
        // TODO mw: read configuration.
        return CompletableFuture.completedFuture(Boolean.FALSE);
    }

    /**
     * Helper function to chain check-methods to ensure user authorization. If all checks fail,
     * an {@link NotAuthorizedException} will be thrown. A single successful result will continue to process (return
     * Done).
     *
     * @param checks The list of checks to execute.
     * @return Done if all checks are positive.
     */
    @SafeVarargs
    public final CompletionStage<Done> withAuthorization(Supplier<CompletionStage<Boolean>>... checks) {
        return isAuthorized(checks).thenCompose(authorized -> {
            if (authorized) {
                return CompletableFuture.completedFuture(Done.getInstance());
            } else {
                return CompletableFuture.failedFuture(NotAuthorizedException.apply("You are not authorized to execute this action."));
            }
        });
    }

}
