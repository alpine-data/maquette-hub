package maquette.core.config;

import com.typesafe.config.Config;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class ServerConfiguration {

    int port;

    String host;

    String userIdHeaderName;

    String userRolesHeaderName;

    String projectHeaderName;

    public static ServerConfiguration apply(Config config) {
        var port = config.getInt("port");
        var host = config.getString("host");
        var userIdHeaderName = config.getString("user-id-header-name");
        var userRolesHeaderName = config.getString("user-roles-header-name");
        var projectHeaderName = config.getString("project-key-header-name");

        return apply(port, host, userIdHeaderName, userRolesHeaderName, projectHeaderName);
    }

}
