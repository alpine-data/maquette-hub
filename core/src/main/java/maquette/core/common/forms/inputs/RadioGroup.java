package maquette.core.common.forms.inputs;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public class RadioGroup implements InputControl {

   String name;

   List<DataItem> items;

   String defaultValue;

   public static RadioGroup apply(String name, String defaultValue) {
      return apply(name, List.of(), defaultValue);
   }

   public RadioGroup withItem(String key, String label, String description) {
      var items = Lists.newArrayList(this.items.iterator());
      items.add(DataItem.apply(key, label, description));
      return apply(name, List.copyOf(items), defaultValue);
   }

   public RadioGroup withItem(String key, String label) {
      return withItem(key, label, null);
   }

   public String getDefaultValue() {
      return defaultValue != null ? defaultValue : items.isEmpty() ? "" : items.get(0).getValue();
   }

}
