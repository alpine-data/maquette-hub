package maquette.datashop.providers.databases.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public class ConnectionTestFailedException extends ApplicationException {

    private ConnectionTestFailedException(String message) {
        super(message);
    }

    public static ConnectionTestFailedException apply() {
        var message = "Connection to database failed.";
        return new ConnectionTestFailedException(message);
    }
}
