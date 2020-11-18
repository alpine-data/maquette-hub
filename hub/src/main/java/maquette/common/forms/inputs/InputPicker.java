package maquette.common.forms.inputs;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public class InputPicker implements InputControl {

   String name;

   List<DataItem> items;

   String defaultValue;

   public static InputPicker apply(String name, String defaultValue) {
      return apply(name, List.of(), defaultValue);
   }

   public InputPicker withItem(String key, String label) {
      var items = Lists.newArrayList(this.items.iterator());
      items.add(DataItem.apply(key, label));

      return apply(name, List.copyOf(items), defaultValue);
   }

}
