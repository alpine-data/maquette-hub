package maquette.core.common.exceptions;

/**
 * An {@link ApplicationException} is an exception which is usually caused due to faulty user requests.
 * The error message of these exceptions is displayed to users.
 */
public class ApplicationException extends RuntimeException {

    public int getHttpStatus() {
        return 400;
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ApplicationException apply(String message, Object... args) {
        return new ApplicationException(String.format(message, args));
    }

}
