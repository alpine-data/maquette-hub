package maquette.common.apidocs;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.compress.utils.Lists;

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
