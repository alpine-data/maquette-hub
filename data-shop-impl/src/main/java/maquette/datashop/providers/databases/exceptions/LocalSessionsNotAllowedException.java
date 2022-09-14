package maquette.datashop.providers.databases.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public class LocalSessionsNotAllowedException extends ApplicationException {

    private LocalSessionsNotAllowedException(String message) {
        super(message);
    }

    public static LocalSessionsNotAllowedException apply() {
        String message = "Using local sessions is not allowed.";
        return new LocalSessionsNotAllowedException(message);
    }

}
