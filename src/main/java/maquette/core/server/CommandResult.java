package maquette.core.server;

import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;

public interface CommandResult {

    default String toPlainText(RuntimeConfiguration runtime) {
        return Operators.suppressExceptions(() -> runtime.getObjectMapper().writeValueAsString(this));
    }

}
