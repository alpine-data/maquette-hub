package maquette.core.services;

import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.processes.model.ProcessSummary;
import maquette.core.entities.projects.Project;
import maquette.core.entities.projects.Projects;
import maquette.core.entities.sandboxes.Sandbox;
import maquette.core.entities.sandboxes.Sandboxes;
import maquette.core.entities.sandboxes.exceptions.SandboxNotFoundException;
import maquette.core.entities.sandboxes.model.SandboxDetails;
import maquette.core.entities.sandboxes.model.SandboxProperties;
import maquette.core.entities.sandboxes.model.stacks.*;
import maquette.core.values.exceptions.ProjectNotFoundException;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public class SandboxServicesImpl implements SandboxServices {

   private final ProcessManager processesManager;

   private final InfrastructureManager infrastructure;

   private final Projects projects;

   private final Sandboxes sandboxes;

   @Override
   public CompletionStage<SandboxDetails> createSandbox(User user, String projectName, String name, List<StackConfiguration> stacks) {
      return withProjectByName(projectName, project -> sandboxes
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
            var deployments = stacks
               .stream()
               .map(config -> {
                  var stack = Stacks.apply().getStackByConfiguration(config);
                  var projectPropertiesCS = project.getProperties();
                  var sandboxPropertiesCS = sandbox.getProperties();

                  return Operators.compose(
                     projectPropertiesCS, sandboxPropertiesCS,
                     (projectProperties, sandboxProperties) -> {
                        var deployment = stack.getDeploymentConfig(projectProperties, sandboxProperties, config);
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
               .thenCompose(this::enrichSandboxProperties);
         }));
   }

   @Override
   public CompletionStage<SandboxDetails> getSandbox(User user, String project, String sandbox) {
      return withSandboxByName(project, sandbox, (p, s) -> s.getProperties().thenCompose(this::enrichSandboxProperties));
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
      return withProjectByName(project, p -> sandboxes.listSandboxes(p.getId()));
   }

   private CompletionStage<SandboxDetails> enrichSandboxProperties(SandboxProperties properties) {
      var deployedStacksCS = Operators.allOf(
         properties
            .getStacks()
            .stream()
            .map(stack -> infrastructure
               .getDeployment(stack.getDeployment()).orElseThrow()
               .getProperties()
               .thenApply(deploymentProperties -> {
                  var stackConfiguration = stack.getConfiguration();
                  var stackParameters = Stacks.apply()
                     .getStackByConfiguration(stackConfiguration)
                     .getParameters(deploymentProperties, stackConfiguration);

                  return DeployedStackDetails.apply(deploymentProperties, stackConfiguration, stackParameters);
               }))
            .collect(Collectors.toList()));

      var processesCS = Operators.allOf(
         properties
            .getProcesses()
            .stream()
            .map(processesManager::getDetails)
            .collect(Collectors.toList()));

      return Operators.compose(deployedStacksCS, processesCS, (deployments, processes) -> {
         var processesFiltered = processes
            .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(d -> ProcessSummary.apply(d.getPid(), d.getStatus().getCreated(), d.getDescription(), d.getStatus().toString()))
            .collect(Collectors.toList());

         return SandboxDetails.apply(
            properties.getId(),
            properties.getName(),
            deployments,
            processesFiltered);
      });
   }

   private <T> CompletionStage<T> withProjectByName(String projectName, Function<Project, CompletionStage<T>> func) {
      return projects
         .findProjectByName(projectName)
         .thenCompose(maybeProject -> {
            if (maybeProject.isPresent()) {
               return func.apply(maybeProject.get());
            } else {
               throw ProjectNotFoundException.applyFromName(projectName);
            }
         });
   }

   private <T> CompletionStage<T> withSandboxByName(String projectName, String sandboxName, BiFunction<Project, Sandbox, CompletionStage<T>> func) {
      return withProjectByName(projectName, project -> sandboxes
         .findSandboxByName(project.getId(), sandboxName)
         .thenCompose(maybeSandbox -> {
            if (maybeSandbox.isPresent()) {
               return func.apply(project, maybeSandbox.get());
            } else {
               throw SandboxNotFoundException.apply(sandboxName);
            }
         }));
   }
}
