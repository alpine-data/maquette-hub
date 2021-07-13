package maquette.core.values.apidocs;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class Item {

   String name;

   Request request;

   public List<Object> getResponse() {
      return Lists.newArrayList();
   }

}
