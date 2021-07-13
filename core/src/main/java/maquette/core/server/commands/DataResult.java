package maquette.core.server.commands;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;

/**
 * Return any kind of structured data, usually some JSON object tree.
 *
 * @param <T> The actual type of the data.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class DataResult<T> implements CommandResult {

   T data;

   @Override
   public String toPlainText(MaquetteRuntime runtime) {
      return Operators.suppressExceptions(() -> runtime
         .getObjectMapperFactory()
         .createJsonMapper(true)
         .writeValueAsString(data));
   }

}
