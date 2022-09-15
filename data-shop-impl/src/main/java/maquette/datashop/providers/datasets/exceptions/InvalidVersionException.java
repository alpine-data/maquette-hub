package maquette.datashop.providers.datasets.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public final class InvalidVersionException extends ApplicationException {

    private InvalidVersionException(String message) {
        super(message);
    }

    public static InvalidVersionException apply(String version) {
        String message = String.format(
            "The provided version '%s' is not a valid.",
            version);

        return new InvalidVersionException(message);
    }

}
