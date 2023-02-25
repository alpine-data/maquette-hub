package maquette.core.modules.users.exceptions;

import maquette.core.values.user.User;

/*
 * This exception is not an ApplicationException. If this exception is fired,
 * it's caused by wrong design of a function. Thus, it's an internal exception
 */
public final class NoAuthenticatedUserException extends RuntimeException {

    private NoAuthenticatedUserException(String message) {
        super(message);
    }

    public static NoAuthenticatedUserException apply(User user) {
        String message = String.format("The current user `%s` is not an authenticated, personal user.", user.getDisplayName());
        return new NoAuthenticatedUserException(message);
    }

}
