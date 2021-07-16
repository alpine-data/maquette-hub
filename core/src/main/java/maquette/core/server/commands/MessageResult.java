package maquette.core.server.commands;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;

/**
 * Return a single message as result of an operation.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class MessageResult implements CommandResult {

   String message;

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
   public static MessageResult apply(String s, Object... args) {
      return apply(String.format(s, args));
   }

}