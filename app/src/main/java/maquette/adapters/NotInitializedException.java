package maquette.adapters;

public class NotInitializedException extends RuntimeException {

    private NotInitializedException(String message) {
        super(message);
    }

    public static NotInitializedException apply(String module) {
        var message = String.format("`%s` has not been initialized yet. Other modules should not be used during " +
            "initialization.", module);
        return new NotInitializedException(message);
    }

}
