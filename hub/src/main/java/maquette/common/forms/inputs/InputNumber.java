package maquette.common.forms.inputs;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class InputNumber implements InputControl {

   String name;

   Integer defaultValue;

   Integer min;

   Integer max;

   public static InputNumber apply(String name, int defaultValue) {
      return apply(name, defaultValue, null, null);
   }

   public InputNumber withMin(int min) {
      return apply(name, defaultValue, min, max);
   }

   public InputNumber withMax(int max) {
      return apply(name, defaultValue, min, max);
   }

}
