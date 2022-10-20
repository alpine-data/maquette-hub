package maquette.datashop.providers.databases.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public class QueryNamesMustBeUniqueException extends ApplicationException {

    private QueryNamesMustBeUniqueException(String message) {
        super(message);
    }

    public static QueryNamesMustBeUniqueException apply() {
        String message = "All queries must have a unique name for the Database Data Asset.";
        return new QueryNamesMustBeUniqueException(message);
    }

}
