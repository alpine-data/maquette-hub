package maquette.core.entities.logs;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class Action {

   ActionCategory category;

   String message;

   public static Action apply(ActionCategory category, String message, Object... parameters) {
      return apply(category, String.format(message, parameters));
   }

}
