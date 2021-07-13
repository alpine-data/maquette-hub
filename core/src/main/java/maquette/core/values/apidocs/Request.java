package maquette.core.values.apidocs;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.List;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Request {

   HttpMethod method;

   List<TextField> header;

   Body body;

   Url url;

   public static Request apply() {
      return apply(HttpMethod.GET, Lists.newArrayList(), null, Url.apply());
   }

   public Request withHeader(String key, String value) {
      this.header.add(TextField.apply(key, value));
      return this;
   }

}
