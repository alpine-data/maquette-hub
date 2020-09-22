package maquette;

import maquette.adapters.DockerInfrastructureProvider;
import maquette.core.CoreApp;
import maquette.core.config.ApplicationConfiguration;

public class Application {

    public static void main(String[] args) {
        var config = ApplicationConfiguration.apply();
        var infrastructure = DockerInfrastructureProvider.apply();

        // CoreApp.apply(config, infrastructure).run();
    }

}
