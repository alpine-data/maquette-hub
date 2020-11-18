package maquette.common.forms.inputs;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public class CheckboxGroup implements InputControl {

   String name;

   List<DataItem> items;

   String defaultValue;

   public static CheckboxGroup apply(String name) {
      return apply(name, List.of(), null);
   }

   public CheckboxGroup withItem(String key, String label, String description) {
      var items = Lists.newArrayList(this.items.iterator());
      items.add(DataItem.apply(key, label, description));

      return apply(name, List.copyOf(items), defaultValue);
   }

   public CheckboxGroup withItem(String key, String label) {
      return withItem(key, label, null);
   }

   public CheckboxGroup withDefaultValue(String value) {
      return apply(name, items, value);
   }

}
