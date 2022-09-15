package maquette.datashop.providers.datasets.exceptions;

import maquette.core.common.exceptions.ApplicationException;
import maquette.datashop.providers.datasets.model.DatasetVersion;

public final class VersionNotFoundException extends ApplicationException {

    private VersionNotFoundException(String message) {
        super(message);
    }

    public static VersionNotFoundException apply(String version) {
        var msg = String.format("Dataset does not contain the version `%s`", version);
        return new VersionNotFoundException(msg);
    }

    public static VersionNotFoundException apply(DatasetVersion version) {
        return apply(version.toString());
    }

}
