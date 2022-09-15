package maquette.development.values.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public final class ModelVersionNotFoundException extends ApplicationException {

    private ModelVersionNotFoundException(String message) {
        super(message);
    }

    public static ModelVersionNotFoundException apply(String model, String version) {
        var msg = String.format("Model version `%s` does not exist in model `%s`.", version, model);
        return new ModelVersionNotFoundException(msg);
    }

}
