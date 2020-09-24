package maquette.core.server.results;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.CommandResult;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataResult<T> implements CommandResult {

    T data;

    @Override
    public String toPlainText(RuntimeConfiguration runtime) {
        return Operators.suppressExceptions(() -> runtime.getObjectMapper().writeValueAsString(data));
    }

}
