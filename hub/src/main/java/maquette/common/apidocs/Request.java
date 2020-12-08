package maquette.common.apidocs;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import org.apache.commons.compress.utils.Lists;

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
