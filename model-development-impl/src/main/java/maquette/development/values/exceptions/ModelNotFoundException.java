package maquette.development.values.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public final class ModelNotFoundException extends ApplicationException {

    private ModelNotFoundException(String message) {
        super(message);
    }

    public static ModelNotFoundException apply(String model) {
        var msg = String.format("Model `%s` does not exist.", model);
        return new ModelNotFoundException(msg);
    }

}
