package maquette.datashop.providers.databases.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public class QueryNotFoundException extends ApplicationException {

    private QueryNotFoundException(String message) {
        super(message);
    }

    public static QueryNotFoundException applyWithId(String database, String queryId) {
        String message = "No database found with id `" + queryId + "` in database data asset `" + database + "`.";
        return new QueryNotFoundException(message);
    }

    public static QueryNotFoundException applyWithId(String queryId) {
        String message = "No database found with id `" + queryId + "`.";
        return new QueryNotFoundException(message);
    }

    public static QueryNotFoundException applyWithName(String queryName) {
        String message = "No database found with name `" + queryName + "`.";
        return new QueryNotFoundException(message);
    }

}
