package maquette.core.config;

import com.typesafe.config.Config;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * The services configuration contains authentication keys for Maquette Services. If a call is authenticated with
 * these secrets, the ServiceUser is instead which has various rights by default.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class ServicesConfiguration {

   String key;

   String secret;

   public static ServicesConfiguration apply(Config config) {
      var key = config.getString("key");
      var secret = config.getString("secret");

      return apply(key, secret);
   }

}
