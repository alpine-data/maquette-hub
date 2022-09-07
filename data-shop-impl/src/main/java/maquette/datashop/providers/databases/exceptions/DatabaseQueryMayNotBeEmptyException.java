package maquette.datashop.providers.databases.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public class DatabaseQueryMayNotBeEmptyException extends ApplicationException {

    private DatabaseQueryMayNotBeEmptyException(String message) {
        super(message);
    }

    public static DatabaseQueryMayNotBeEmptyException apply(String queryName) {
        String message = "The query string for query `" + queryName + "` may not be empty.";
        return new DatabaseQueryMayNotBeEmptyException(message);
    }

}
