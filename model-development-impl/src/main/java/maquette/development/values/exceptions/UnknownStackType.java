package maquette.development.values.exceptions;

import maquette.development.values.stacks.StackConfiguration;

public class UnknownStackType extends RuntimeException {

    private UnknownStackType(String message) {
        super(message);
    }

    public static UnknownStackType apply(StackConfiguration config) {
        var message = String.format("The stack type of stack `%s` cannot be determined from its configuration (%s).",
            config.getStackInstanceName(), config
                .getClass()
                .getName());
        return new UnknownStackType(message);
    }

}
