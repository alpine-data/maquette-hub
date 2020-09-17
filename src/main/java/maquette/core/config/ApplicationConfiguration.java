package maquette.core.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class ApplicationConfiguration {

    String version;

    String environment;

    ServerConfiguration server;

    public static ApplicationConfiguration apply() {
        return apply(ConfigFactory.load());
    }

    public static ApplicationConfiguration apply(Config config) {
        String version = config.getString("maquette.version");
        String environment = config.getString("maquette.environment");
        ServerConfiguration server = ServerConfiguration.apply(config.getConfig("maquette.server"));

        return apply(version, environment, server);
    }

}
