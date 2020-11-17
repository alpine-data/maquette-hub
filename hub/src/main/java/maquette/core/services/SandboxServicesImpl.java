package maquette.core.services;

import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.infrastructure.Deployment;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.infrastructure.model.DeploymentConfigs;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.processes.model.ProcessSummary;
import maquette.core.entities.projects.Project;
import maquette.core.entities.projects.Projects;
import maquette.core.entities.sandboxes.Sandbox;
import maquette.core.entities.sandboxes.Sandboxes;
import maquette.core.entities.sandboxes.exceptions.SandboxNotFoundException;
import maquette.core.entities.sandboxes.model.SandboxDetails;
import maquette.core.entities.sandboxes.model.SandboxProperties;
import maquette.core.values.exceptions.ProjectNotFoundException;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Optional;
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
   public CompletionStage<SandboxDetails> createSandbox(User user, String projectName, String name) {
      return withProjectByName(projectName, project -> sandboxes
         .createSandbox(project.getId(), name)
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
            var deployment = DeploymentConfigs.sample(project.getId(), sandbox.getId());

            return processesManager
               .schedule(
                  user, "initializing sandbox",
                  log -> infrastructure
                     .applyConfig(deployment)
                     .thenCompose(done -> sandbox.addDeployment(deployment.getName())))
               .thenCompose(sandbox::addProcess)
               .thenCompose(done -> sandbox.getProperties())
               .thenCompose(this::enrichSandboxProperties);
         }));
   }

   @Override
   public CompletionStage<SandboxDetails> getSandbox(User user, String project, String sandbox) {
      return withSandboxByName(project, sandbox, (p, s) -> s.getProperties().thenCompose(this::enrichSandboxProperties));
   }

   @Override
   public CompletionStage<List<SandboxProperties>> listSandboxes(User user, String project) {
      return withProjectByName(project, p -> sandboxes.listSandboxes(p.getId()));
   }

   private CompletionStage<SandboxDetails> enrichSandboxProperties(SandboxProperties properties) {
      var deploymentsCS = Operators.allOf(
         properties
            .getDeployments()
            .stream()
            .map(infrastructure::getDeployment)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(Deployment::getProperties)
            .collect(Collectors.toList()));

      var processesCS = Operators.allOf(
         properties
            .getProcesses()
            .stream()
            .map(processesManager::getDetails)
            .collect(Collectors.toList()));

      return Operators.compose(deploymentsCS, processesCS, (deployments, processes) -> {
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
