package maquette.core.server.commands;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.MaquetteRuntime;

import javax.annotation.Nullable;

/**
 * Return a single message as result of an operation.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class MessageResult implements CommandResult {

    String message;

    @With
    @Nullable
    Object data;

    public static MessageResult apply(String message) {
        return apply(message, null);
    }

    @Override
    public String toPlainText(MaquetteRuntime runtime) {
        return message;
    }

    /**
     * Create a new message result. The method uses String.format under the hood to substitute variable placeholders.
     *
     * @param s    The messages
     * @param args Variables for substitution
     * @return The message result object
     */
    public static MessageResult create(String s, Object... args) {
        return apply(String.format(s, args), null);
    }

}
