package maquette.core.common.validation.api;

public class ValidationExecutionException extends RuntimeException {

    private ValidationExecutionException(String message, Exception cause) {
        super(message, cause);
    }

    public static ValidationExecutionException apply(String fieldName, Object value, Exception cause) {
        var msg = String.format("An exception occurred during validation of field `%s` with value `%s`.", fieldName, value);
        return new ValidationExecutionException(msg, cause);
    }

}
