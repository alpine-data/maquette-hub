package maquette.development.values.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public final class VolumeAlreadyExistsException extends ApplicationException {

    private VolumeAlreadyExistsException(String message) {
        super(message);
    }

    public static VolumeAlreadyExistsException apply(String volumeName, String workspace) {
        var msg = String.format("Volume `%s` already exists in `%s` workspace.", volumeName, workspace);
        return new VolumeAlreadyExistsException(msg);
    }

}
