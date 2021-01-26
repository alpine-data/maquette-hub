package maquette.streams.core.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class ApplicationConfiguration {

   int port;

   String host;

   public static ApplicationConfiguration apply(Config config) {
      var port = config.getInt("port");
      var host = config.getString("host");

      return apply(port, host);
   }

   public static ApplicationConfiguration apply() {
      return apply(ConfigFactory.load().getConfig("maquette.streams"));
   }

}
