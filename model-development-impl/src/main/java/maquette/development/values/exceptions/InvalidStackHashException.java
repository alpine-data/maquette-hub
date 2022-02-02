package maquette.development.values.exceptions;

import maquette.core.common.exceptions.ApplicationException;
import maquette.core.values.UID;

public class InvalidStackHashException extends ApplicationException {

    private InvalidStackHashException(String message) {
        super(message);
    }

    public static InvalidStackHashException apply(UID workspace, UID sandbox, String hash) {
        String msg = String.format("The hash `%s` is not valid for sandbox `%s/%s`", hash, workspace, sandbox);
        return new InvalidStackHashException(msg);
    }

}
