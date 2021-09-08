package maquette.core.common.exceptions;

public class NotAuthorizedException extends ApplicationException {

    private NotAuthorizedException(String message) {
        super(message);
    }

    public static NotAuthorizedException apply(String message) {
        return new NotAuthorizedException(message);
    }

    @Override
    public int getHttpStatus() {
        return 401;
    }
}
