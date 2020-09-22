package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.CommandResult;

@Value
@AllArgsConstructor(staticName = "apply")
public class MessageResult implements CommandResult {

    String message;

    @Override
    public String toPlainText(RuntimeConfiguration runtime) {
        return message;
    }

}
