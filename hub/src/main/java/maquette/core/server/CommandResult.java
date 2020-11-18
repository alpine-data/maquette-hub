package maquette.core.server;

import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;

import java.util.Optional;

public interface CommandResult {

    default String toPlainText(RuntimeConfiguration runtime) {
        return Operators.suppressExceptions(() -> runtime.getObjectMapper().writeValueAsString(this));
    }

    default Optional<String> toCSV(RuntimeConfiguration runtime) {
        return Optional.empty();
    }

}
