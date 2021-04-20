package maquette.core.services.sandboxes;

import akka.japi.Pair;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.infrastructure.model.DataVolume;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.projects.ProjectEntity;
import maquette.core.entities.projects.SandboxEntities;
import maquette.core.entities.projects.SandboxEntity;
import maquette.core.entities.users.exceptions.MissingGitSettings;
import maquette.core.entities.projects.model.sandboxes.Sandbox;
import maquette.core.entities.projects.model.sandboxes.SandboxProperties;
import maquette.core.entities.projects.model.sandboxes.stacks.*;
import maquette.core.entities.projects.model.sandboxes.volumes.ExistingVolume;
import maquette.core.entities.projects.model.sandboxes.volumes.GitVolume;
import maquette.core.entities.projects.model.sandboxes.volumes.PlainVolume;
import maquette.core.entities.projects.model.sandboxes.volumes.VolumeDefinition;
import maquette.core.entities.users.UserEntities;
import maquette.core.entities.users.UserEntity;
import maquette.core.entities.users.model.UserSettings;
import maquette.core.services.projects.EnvironmentType;
import maquette.core.services.projects.ProjectCompanion;
import maquette.core.values.data.binary.BinaryObjects;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import scala.Tuple3;

import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class SandboxServicesImpl implements SandboxServices {

   private final ProcessManager processesManager;

   private final InfrastructureManager infrastructure;

   private final ProjectEntities projects;

   private final SandboxEntities sandboxes;

   private final UserEntities users;

   private final ProjectCompanion projectCompanion;

   private final SandboxCompanion sandboxCompanion;

   @Override
   public CompletionStage<Sandbox> createSandbox(
      User user, String projectName, String name, VolumeDefinition volume, List<StackConfiguration> stacks) {


      return projects
         .getProjectByName(projectName)
         .thenCompose(project -> createVolume(user, volume).thenApply(v -> Pair.apply(project, v)))
         .thenCompose(pair -> {
            var project = pair.first();
            var createdVolume = pair.second();
            return sandboxes
               .createSandbox(user, project.getId(), createdVolume.getId(), name)
               .thenCompose(sandbox -> sandboxes.getSandboxByName(project.getId(), sandbox.getName()))
               .thenApply(sandbox -> Tuple3.apply(project, createdVolume, sandbox));
         })
         .thenCompose(tuple -> {
            var project = tuple._1();
            var createdVolume = tuple._2();
            var sandbox = tuple._3();

            var projectPropertiesCS = project.getProperties();
            var projectEnvironmentCS = projectCompanion.environment(projectName, EnvironmentType.SANDBOX);
            var sandboxPropertiesCS = sandbox.getProperties();

            var deployments = stacks
               .stream()
               .map(config -> {
                  var stack = Stacks.apply().getStackByConfiguration(config);

                  return Operators.compose(
                     projectPropertiesCS, sandboxPropertiesCS, projectEnvironmentCS,
                     (projectProperties, sandboxProperties, projectEnvironment) -> {
                        var deployment = stack.getDeploymentConfig(
                           projectProperties, sandboxProperties, createdVolume, config, projectEnvironment);

                        var processDescription = String.format(
                           "initializing stack `%s` for sandbox `%s/%s`",
                           config.getStackName(), projectProperties.getName(), sandboxProperties.getName());

                        return processesManager
                           .schedule(user, processDescription, log -> infrastructure
                              .applyConfig(deployment)
                              .thenCompose(deploymentProperties -> {
                                 var deployed = DeployedStackProperties.apply(
                                    deployment.getName(),
                                    config);

                                 return sandbox.addDeployment(deployed);
                              }))
                           .thenCompose(sandbox::addProcess);
                     });
               })
               .collect(Collectors.toList());

            return Operators
               .allOf(deployments)
               .thenCompose(d -> sandbox.getProperties())
               .thenCompose(sandboxCompanion::enrichSandboxProperties);
         });
   }

   @Override
   public CompletionStage<Sandbox> getSandbox(User user, String project, String sandbox) {
      return withSandboxByName(project, sandbox, (p, s) -> s.getProperties().thenCompose(sandboxCompanion::enrichSandboxProperties));
   }

   @Override
   public CompletionStage<List<StackProperties>> getStacks(User user) {
      var result = Stacks.apply()
         .getStacks()
         .stream()
         .map(Stack::getProperties)
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<List<SandboxProperties>> getSandboxes(User user, String project) {
      return projects
         .getProjectByName(project)
         .thenCompose(p -> sandboxes.listSandboxes(p.getId()));
   }

   private <T> CompletionStage<T> withSandboxByName(String projectName, String sandboxName, BiFunction<ProjectEntity, SandboxEntity, CompletionStage<T>> func) {
      return projects.getProjectByName(projectName)
         .thenCompose(project -> sandboxes
            .getSandboxByName(project.getId(), sandboxName)
            .thenCompose(sandbox -> func.apply(project, sandbox)));
   }

   private CompletionStage<DataVolume> createVolume(User user, VolumeDefinition definition) {
      if (definition instanceof ExistingVolume) {
         // TODO: Check that volume belongs to project?
         return infrastructure.getDataVolumes().getById(((ExistingVolume) definition).getId());
      } else if (definition instanceof PlainVolume) {
         return infrastructure.getDataVolumes().create(user, ((PlainVolume) definition).getName());
      } else if (definition instanceof GitVolume) {
         var settingsCS = Operators
            .flatOptCS(Optional
               .of(user)
               .flatMap(usr -> {
                  if (usr instanceof AuthenticatedUser) {
                     return Optional.of((AuthenticatedUser) usr);
                  } else {
                     return Optional.empty();
                  }
               })
               .map(usr -> users
                  .getUserById(usr.getId())
                  .thenCompose(UserEntity::getSettings)
                  .thenApply(UserSettings::getGit)))
            .thenApply(settings -> settings.orElseThrow(() -> MissingGitSettings.apply(user)));

         return settingsCS.thenCompose(gitSettings -> Operators.suppressExceptions(() -> {
            var gitClient = new GitHubBuilder().withPassword(gitSettings.getUsername(), gitSettings.getPassword()).build();
            var repo = gitClient.getRepository(((GitVolume) definition).getRepository());
            var workingDir = Files.createTempDirectory("mq-");

            var git = Git
               .cloneRepository()
               .setURI(repo.getHttpTransportUrl())
               .setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitSettings.getUsername(), gitSettings.getPassword()))
               .setDirectory(workingDir.toFile())
               .call();
            git.remoteRemove().setRemoteName("origin").call();
            git.remoteAdd().setName("origin").setUri(new URIish(repo.getGitTransportUrl()));

            var compressed = BinaryObjects.fromDirectory(workingDir);
            Operators.ignoreExceptions(() -> FileUtils.deleteDirectory(workingDir.toFile()));
            return infrastructure.getDataVolumes().create(user, ((GitVolume) definition).getName(), compressed);
         }));
      } else {
         throw new IllegalArgumentException("unknown volume definition");
      }
   }

}
