package maquette.core;

import akka.actor.ActorSystem;
import io.javalin.Javalin;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.ObjectMapperFactory;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.project.Projects;
import maquette.core.ports.InfrastructureProvider;
import maquette.core.ports.InfrastructureRepository;
import maquette.core.ports.ProjectsRepository;
import maquette.core.server.MaquetteServer;
import maquette.core.services.ApplicationServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Value
@AllArgsConstructor(staticName = "apply")
public class CoreApp {

    private static final Logger LOG = LoggerFactory.getLogger(CoreApp.class);

    RuntimeConfiguration runtime;

    MaquetteServer server;

    public void run() {
        /*
        var system = ActorSystem.apply("maquette");

        var app = Javalin
                .create()
                .start(configuration.getServer().getHost(), configuration.getServer().getPort());

        var om = ObjectMapperFactory.apply().create(true);

        var infrastructureManager = InfrastructureManager.apply(infrastructureProvider, infrastructureRepository);
        var processManager = ProcessManager.apply();
        var projects = Projects.apply(projectsRepository);
        var runtime = RuntimeConfiguration.apply(app, system, om, infrastructureManager, processManager, projects);
        var services = ApplicationServices.apply(runtime);

        MaquetteServer.apply(configuration, runtime, services);

        /*
        var runtime = RuntimeConfiguration.apply(
                app, system, om,
                infrastructureProvider, infrastructureRepository, projectsRepository);
    */




        System.out.println("-------------");
    }

    public static CoreApp apply(
            ApplicationConfiguration configuration, InfrastructureProvider infrastructureProvider,
            InfrastructureRepository infrastructureRepository, ProjectsRepository projectsRepository) {

        return null;
    }

}
