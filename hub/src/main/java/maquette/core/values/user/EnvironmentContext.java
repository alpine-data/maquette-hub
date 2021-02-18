package maquette.core.values.user;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class EnvironmentContext {

   EnvironmentType type;

   String id;

   public static EnvironmentContext fromString(String s) {
      var components = s.split(";");

      if (components.length < 2) {
         throw new IllegalArgumentException("Environment context must contain two components `type;id`");
      }

      return EnvironmentContext.apply(EnvironmentType.valueOf(components[0]), components[1]);
   }

}
