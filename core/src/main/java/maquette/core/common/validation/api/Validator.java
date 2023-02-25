package maquette.core.common.validation.api;

public interface Validator<T> {

    /**
     * This method validates the value. Return true to continue validation process, false if validation should
     * fail immediately.
     * <p>
     * Validation errors can be added to the context during validation.
     *
     * @param context   The validation context.
     * @param fieldName The name of the parameter/ field which has been passed by the user.
     * @param value     The value to validate.
     * @return boolean which indicates whether validation should proceed or not.
     */
    boolean validate(ValidationContext context, String fieldName, T value) throws Exception;

    /**
     * Will be called if validate has thrown an Exception.
     * This will actually throw an exception because such an exception
     *
     * @param context   The validation context
     * @param fieldName The name of the parameter/ field which has been passed by the user.
     * @param value     The value which has been validated.
     * @param ex        The exception which was thrown.
     */
    default void onException(ValidationContext context, String fieldName, T value, Exception ex) {
        throw ValidationExecutionException.apply(fieldName, value, ex);
    }

}
