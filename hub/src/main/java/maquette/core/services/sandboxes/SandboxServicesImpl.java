package maquette.core.services.sandboxes;

import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.projects.ProjectEntity;
import maquette.core.entities.sandboxes.SandboxEntities;
import maquette.core.entities.sandboxes.SandboxEntity;
import maquette.core.entities.sandboxes.exceptions.SandboxNotFoundException;
import maquette.core.entities.sandboxes.model.Sandbox;
import maquette.core.entities.sandboxes.model.SandboxProperties;
import maquette.core.entities.sandboxes.model.stacks.*;
import maquette.core.services.projects.EnvironmentType;
import maquette.core.services.projects.ProjectCompanion;
import maquette.core.services.projects.ProjectServices;
import maquette.core.values.user.User;

import java.util.List;
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

   private final ProjectCompanion projectCompanion;

   private final SandboxCompanion sandboxCompanion;

   @Override
   public CompletionStage<Sandbox> createSandbox(User user, String projectName, String name, List<StackConfiguration> stacks) {
      return projects
         .getProjectByName(projectName)
         .thenCompose(project -> sandboxes
            .createSandbox(user, project.getId(), name)
            .thenCompose(properties -> sandboxes
               .findSandboxByName(project.getId(), properties.getName())
               .thenApply(maybeSandbox -> {
                  if (maybeSandbox.isPresent()) {
                     return maybeSandbox.get();
                  } else {
                     throw SandboxNotFoundException.apply(properties.getId());
                  }
               }))
            .thenCompose(sandbox -> {
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
                           var deployment = stack.getDeploymentConfig(projectProperties, sandboxProperties, config, projectEnvironment);
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
            }));
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
}
