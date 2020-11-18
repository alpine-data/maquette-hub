package maquette.common.forms.inputs;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public class ButtonGroup implements InputControl {

   String name;

   List<DataItem> items;

   public static ButtonGroup apply(String name) {
      return apply(name, List.of());
   }

   public ButtonGroup withButton(String label, String action) {
      var items = Lists.newArrayList(this.items.iterator());
      items.add(DataItem.apply(action, label));

      return apply(name, List.copyOf(items));
   }

}
