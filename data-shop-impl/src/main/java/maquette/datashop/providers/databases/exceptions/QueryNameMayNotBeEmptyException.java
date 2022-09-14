package maquette.datashop.providers.databases.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public class QueryNameMayNotBeEmptyException extends ApplicationException {

    private QueryNameMayNotBeEmptyException(String message) {
        super(message);
    }

    public static QueryNameMayNotBeEmptyException apply() {
        String message = "A query must have a unique name, the name may not be empty.";
        return new QueryNameMayNotBeEmptyException(message);
    }

}
