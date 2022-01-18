package maquette.development.values.sandboxes.volumes;

import maquette.core.common.exceptions.ApplicationException;

public final class InvalidVolumeNameException extends ApplicationException {

    private InvalidVolumeNameException(String message) {
        super(message);
    }

    public static InvalidVolumeNameException apply(String name) {
        var message = String.format("The provided volume name `%s` is not valid. Volume names must be kebab-case, " +
            "starting with a letter and a minimum length of 3 characters.", name);
        return new InvalidVolumeNameException(message);
    }

}
