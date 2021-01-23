package maquette.sdk.config.authentication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@With
@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class DataAssetKeyAuthentication implements AuthenticationConfiguration {

   public static final String TYPE_KEY = "data-asset-key";

   String key;

   String secret;

   @JsonIgnore
   public String getTypeKey() {
      return TYPE_KEY;
   }

}
