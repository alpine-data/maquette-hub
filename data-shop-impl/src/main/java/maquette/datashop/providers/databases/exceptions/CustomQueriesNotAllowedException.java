package maquette.datashop.providers.databases.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public class CustomQueriesNotAllowedException extends ApplicationException {

    private CustomQueriesNotAllowedException(String message) {
        super(message);
    }

    public static CustomQueriesNotAllowedException apply() {
        String message = "Custom queries are only allowed when allow custom queries is true.";
        return new CustomQueriesNotAllowedException(message);
    }

}
