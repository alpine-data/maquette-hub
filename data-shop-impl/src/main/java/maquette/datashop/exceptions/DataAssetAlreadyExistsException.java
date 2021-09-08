package maquette.datashop.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public final class DataAssetAlreadyExistsException extends ApplicationException {

    private DataAssetAlreadyExistsException(String message) {
        super(message);
    }

    public static DataAssetAlreadyExistsException withName(String name) {
        var msg = String.format("A data asset with name `%s` already exists.", name);
        return new DataAssetAlreadyExistsException(msg);
    }

}
