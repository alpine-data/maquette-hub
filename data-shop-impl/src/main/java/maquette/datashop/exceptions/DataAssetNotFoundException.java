package maquette.datashop.exceptions;

import maquette.core.common.exceptions.ApplicationException;
import maquette.core.values.UID;

public final class DataAssetNotFoundException extends ApplicationException {

    private DataAssetNotFoundException(String message) {
        super(message);
    }

    public static DataAssetNotFoundException applyFromName(String name) {
        String msg = String.format("Data Asset with name `%s` was not found.", name);
        return new DataAssetNotFoundException(msg);
    }

    public static DataAssetNotFoundException applyFromId(UID asset) {
        String msg = String.format("Data Asset with id `%s` was not found.", asset);
        return new DataAssetNotFoundException(msg);
    }

}
