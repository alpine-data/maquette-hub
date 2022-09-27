package maquette.development.values.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public final class VolumeDoesntExistException extends ApplicationException {

    private VolumeDoesntExistException(String message) {
        super(message);
    }

    public static VolumeDoesntExistException apply(String volumeName, String workspace) {
        var msg = String.format("Volume `%s` doesn't exist in `%s` workspace.", volumeName, workspace);
        return new VolumeDoesntExistException(msg);
    }

}
