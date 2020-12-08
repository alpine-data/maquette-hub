package maquette.core;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import io.javalin.Javalin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.common.Templates;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.collections.CollectionEntities;
import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasources.DataSourceEntities;
import maquette.core.entities.data.streams.StreamEntities;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.sandboxes.SandboxEntities;
import maquette.core.entities.users.Users;
import maquette.core.ports.*;
import maquette.core.server.MaquetteServer;
import maquette.core.server.OpenApiResource;
import maquette.core.services.ApplicationServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@AllArgsConstructor(staticName = "apply")
public class CoreApp {

    private static final Logger LOG = LoggerFactory.getLogger(CoreApp.class);

    private final RuntimeConfiguration runtime;

    private final ApplicationServices services;

    private final MaquetteServer server;

    public void stop() {
        server.stop();
    }

    public static CoreApp apply(
       ApplicationConfiguration configuration, InfrastructureProvider infrastructureProvider,
       InfrastructureRepository infrastructureRepository, ProjectsRepository projectsRepository,
       CollectionsRepository collectionsRepository, DatasetsRepository datasetsRepository, RecordsStore recordsStore,
       DataSourcesRepository dataSourceRepository, StreamsRepository streamsRepository,
       SandboxesRepository sandboxesRepository, UsersRepository usersRepository,
       DataExplorer dataExplorer, ObjectMapper om) {

        LOG.info("Starting Maquette Hub Server");

        var system = ActorSystem.apply("maquette");

        var app = Javalin
                .create(config -> {
                    config.showJavalinBanner = false;
                    config.registerPlugin(OpenApiResource.apply(configuration));
                })
                .start(configuration.getServer().getHost(), configuration.getServer().getPort());

        var infrastructureManager = InfrastructureManager.apply(infrastructureProvider, infrastructureRepository);
        var processManager = ProcessManager.apply();
        var projects = ProjectEntities.apply(projectsRepository);

        var collections = CollectionEntities.apply(collectionsRepository);
        var datasets = DatasetEntities.apply(datasetsRepository, recordsStore, dataExplorer);
        var dataSources = DataSourceEntities.apply(dataSourceRepository, recordsStore, dataExplorer);
        var streams = StreamEntities.apply(streamsRepository);

        var sandboxes = SandboxEntities.apply(sandboxesRepository);
        var users = Users.apply(usersRepository);

        var runtime = RuntimeConfiguration.apply(
           app, system, om, collections, datasets, dataSources, streams, infrastructureManager,
           processManager, projects, sandboxes, users);

        var services = ApplicationServices.apply(runtime);

        var server = MaquetteServer.apply(configuration, runtime, services);

        var map = Maps.<String, Object>newHashMap();
        map.put("version", configuration.getVersion());
        map.put("environment", configuration.getEnvironment());
        var banner = Templates.renderTemplateFromResources("banner.twig", map);
        LOG.info("Started Maquette Hub Server {}", banner);

        return CoreApp.apply(runtime, services, server);
    }

}
