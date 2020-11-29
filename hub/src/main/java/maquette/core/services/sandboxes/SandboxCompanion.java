package maquette.core.services.sandboxes;

import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.processes.model.ProcessSummary;
import maquette.core.entities.sandboxes.model.Sandbox;
import maquette.core.entities.sandboxes.model.SandboxProperties;
import maquette.core.entities.sandboxes.model.stacks.DeployedStackDetails;
import maquette.core.entities.sandboxes.model.stacks.Stacks;
import maquette.core.services.ServiceCompanion;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class SandboxCompanion extends ServiceCompanion {

   private final ProcessManager processesManager;

   private final InfrastructureManager infrastructure;

   public CompletionStage<Sandbox> enrichSandboxProperties(SandboxProperties properties) {
      var deployedStacksCS = Operators.allOf(
         properties
            .getStacks()
            .stream()
            .map(stack -> infrastructure
               .getDeployment(stack.getDeployment()).orElseThrow()
               .getProperties()
               .thenCompose(deploymentProperties -> {
                  var stackConfiguration = stack.getConfiguration();
                  var stackParametersCS = Stacks.apply()
                     .getStackByConfiguration(stackConfiguration)
                     .getParameters(deploymentProperties, stackConfiguration);

                  return stackParametersCS.thenApply(stackParameters -> DeployedStackDetails
                     .apply(deploymentProperties, stackConfiguration, stackParameters));
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

         return Sandbox.apply(
            properties.getId(),
            properties.getName(),
            properties.getCreated(),
            deployments,
            processesFiltered);
      });
   }

}
