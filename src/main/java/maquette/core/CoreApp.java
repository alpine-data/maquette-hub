package maquette.core;

import akka.actor.ActorSystem;
import io.javalin.Javalin;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.ports.InfrastructureProviderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Value
@AllArgsConstructor(staticName = "apply")
public class CoreApp {

    private static final Logger LOG = LoggerFactory.getLogger(CoreApp.class);

    ApplicationConfiguration configuration;

    InfrastructureProviderPort infrastructure;

    public void run() {
        var app = Javalin
                .create()
                .start(configuration.getServer().getHost(), configuration.getServer().getPort());
        ActorSystem.apply("test");

        System.out.println("-------------");
        infrastructure
                .createContainer("foo.bar")
                .exceptionally(t -> {
                    LOG.error("Error occurred", t);
                    return null;
                });
    }

}
