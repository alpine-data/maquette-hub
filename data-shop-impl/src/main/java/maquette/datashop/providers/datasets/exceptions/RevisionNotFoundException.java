package maquette.datashop.providers.datasets.exceptions;

import maquette.core.common.exceptions.ApplicationException;
import maquette.core.values.UID;

public final class RevisionNotFoundException extends ApplicationException {

    private RevisionNotFoundException(String message) {
        super(message);
    }

    public static RevisionNotFoundException apply(UID revisionId) {
        var msg = String.format("Dataset does not contain the revision id `%s`", revisionId);
        return new RevisionNotFoundException(msg);
    }

}
