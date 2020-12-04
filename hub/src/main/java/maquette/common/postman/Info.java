package maquette.common.postman;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class Info {

   String name;

   String schema;

   public static Info apply(String name) {
      return apply(name, "https://schema.getpostman.com/json/collection/v2.1.0/collection.json");
   }


}
