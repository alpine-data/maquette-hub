package maquette.core.server.results;

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

    public static MessageResult apply(String s, Object arg1, Object ...args) {
        return apply(String.format(String.format(s, arg1), args));
    }

}
