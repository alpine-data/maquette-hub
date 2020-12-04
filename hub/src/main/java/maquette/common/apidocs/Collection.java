package maquette.common.apidocs;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.compress.utils.Lists;

import java.util.Comparator;
import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class Collection {

   Info info;

   List<Item> item;

   List<Variable> variable;

   public static Collection apply(Info info) {
      return apply(info, Lists.newArrayList(), Lists.newArrayList());
   }

   public static Collection apply(String name) {
      return apply(Info.apply(name));
   }

   public List<Object> getEvent() {
      return Lists.newArrayList();
   }

   public Object protocolProfileBehavior() {
      return new Object();
   }

   public Collection withItem(Item item) {
      this.item.add(item);
      this.item.sort(Comparator.comparing(Item::getName));
      return this;
   }

   public Collection withVariable(Variable variable) {
      this.variable.add(variable);
      return this;
   }

}
