package maquette.datashop.providers.databases.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public class AtLeastOneQueryException extends ApplicationException {

    private AtLeastOneQueryException(String message) {
        super(message);
    }

    public static AtLeastOneQueryException apply() {
        String message = "A Database data asset requires at least one query.";
        return new AtLeastOneQueryException(message);
    }

}
