package maquette.datashop.providers.databases.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public class AllowLocalSessionsOnlyWithCustomQueriesException extends ApplicationException {

    private AllowLocalSessionsOnlyWithCustomQueriesException(String message) {
        super(message);
    }

    public static AllowLocalSessionsOnlyWithCustomQueriesException apply() {
        String message = "Allow local sessions is only allowed when custom queries are allowed as well.";
        return new AllowLocalSessionsOnlyWithCustomQueriesException(message);
    }

}
