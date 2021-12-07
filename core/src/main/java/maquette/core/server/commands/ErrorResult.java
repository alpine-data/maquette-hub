package maquette.core.server.commands;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;

/**
 * Return an error message as result of an operation.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class ErrorResult implements CommandResult {

    String error;

    @Override
    public String toPlainText(MaquetteRuntime runtime) {
        return error;
    }

    /**
     * Create a new error result. The method uses String.format under the hood to substitute variable placeholders.
     *
     * @param s    The error message
     * @param args Variables for substitution
     * @return The message result object
     */
    public static ErrorResult create(String s, Object... args) {
        return apply(String.format(s, args));
    }

}
