package maquette.common.forms.inputs;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class Input implements InputControl {

   String name;

   String defaultValue;

   String placeholder;

   public static Input apply(String name, String defaultValue) {
      return apply(name, defaultValue, null);
   }

   public static Input apply(String name) {
      return apply(name, null);
   }

   public Input withDefaultValue(String value) {
      return apply(name, value, placeholder);
   }

   public Input withPlaceholder(String placeholder) {
      return apply(name, defaultValue, placeholder);
   }

}
