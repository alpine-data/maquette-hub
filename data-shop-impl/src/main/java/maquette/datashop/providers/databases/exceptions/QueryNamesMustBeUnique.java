package maquette.datashop.providers.databases.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public class QueryNamesMustBeUnique extends ApplicationException {

    private QueryNamesMustBeUnique(String message) {
        super(message);
    }

    public static QueryNamesMustBeUnique apply() {
        String message = "All queries must have a unique name for the Database Data Asset.";
        return new QueryNamesMustBeUnique(message);
    }

}
