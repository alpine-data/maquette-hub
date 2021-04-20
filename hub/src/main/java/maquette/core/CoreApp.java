package maquette.core;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import io.javalin.Javalin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.common.Operators;
import maquette.common.Templates;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.data.DataAssetProviders;
import maquette.core.entities.data.ports.DataAssetsRepository;
import maquette.core.entities.dependencies.Dependencies;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.infrastructure.ports.InfrastructureProvider;
import maquette.core.entities.infrastructure.ports.InfrastructureRepository;
import maquette.core.entities.logs.Logs;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.projects.ports.ApplicationsRepository;
import maquette.core.entities.projects.ports.ModelsRepository;
import maquette.core.entities.projects.SandboxEntities;
import maquette.core.entities.users.UserEntities;
import maquette.core.ports.*;
import maquette.core.server.MaquetteServer;
import maquette.core.server.OpenApiResource;
import maquette.core.services.ApplicationServices;
import org.kohsuke.github.GitHubBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class CoreApp {

   private static final Logger LOG = LoggerFactory.getLogger(CoreApp.class);

   private final RuntimeConfiguration runtime;

   private final ApplicationServices services;

   private final MaquetteServer server;

   public void stop() {
      server.stop();
   }

   public static CoreApp apply(
      ApplicationConfiguration configuration,
      InfrastructureProvider infrastructureProvider,
      InfrastructureRepository infrastructureRepository,
      ProjectsRepository projectsRepository,
      ModelsRepository modelsRepository,
      ApplicationsRepository applicationsRepository,
      SandboxesRepository sandboxesRepository,
      UsersRepository usersRepository,
      DataAssetsRepository dataAssetsRepository,
      DataAssetProviders dataAssetProviders,
      MlflowProxyPort mlflowProxyPort,
      ObjectMapper om) {

      LOG.info("Starting Maquette Hub Server");

      var system = ActorSystem.apply("maquette");

      var app = Javalin
         .create(config -> {
            config.showJavalinBanner = false;
            config.registerPlugin(OpenApiResource.apply(configuration));
         })
         .start(configuration.getServer().getHost(), configuration.getServer().getPort());

      var infrastructureManager = InfrastructureManager.apply(infrastructureProvider, infrastructureRepository, mlflowProxyPort);
      var processManager = ProcessManager.apply();
      var projects = ProjectEntities.apply(projectsRepository, modelsRepository, applicationsRepository);
      var dataAssets = DataAssetEntities.apply(dataAssetsRepository, dataAssetProviders.toMap());

      var sandboxes = SandboxEntities.apply(sandboxesRepository);
      var users = UserEntities.apply(usersRepository, om);
      var dependencies = Dependencies.apply();
      var logs = Logs.apply();

      var gitClient = Operators.suppressExceptions(() -> new GitHubBuilder()
         .withPassword(
            configuration.getSettings().getGit().getUsername(),
            configuration.getSettings().getGit().getToken())
         .build());

      var runtime = RuntimeConfiguration.apply(
         app, system, om, dataAssetProviders, dataAssets, infrastructureManager,
         processManager, projects, sandboxes, users, dependencies, logs, gitClient);

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
