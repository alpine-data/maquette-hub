package maquette.core.modules.users.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public final class InvalidAuthenticationTokenException extends ApplicationException {

    private InvalidAuthenticationTokenException(String message) {
        super(message);
    }

    public static InvalidAuthenticationTokenException createUnknownToken(String tokenId) {
        var msg = String.format("The supplied token `%s` cannot be mapped to a user.", tokenId);
        return new InvalidAuthenticationTokenException(msg);
    }

    public static InvalidAuthenticationTokenException createOutdated(String tokenId) {
        var msg = String.format("The supplied token `%s` is not valid anymore. Please refresh your token.", tokenId);
        return new InvalidAuthenticationTokenException(msg);
    }

    @Override
    public int getHttpStatus() {
        return 401;
    }
}
