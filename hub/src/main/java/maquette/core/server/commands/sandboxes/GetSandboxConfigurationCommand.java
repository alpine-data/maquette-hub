package maquette.core.server.commands.sandboxes;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.projects.model.sandboxes.stacks.DeployedStackDetails;
import maquette.core.entities.projects.model.sandboxes.stacks.StackConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.DataResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GetSandboxConfigurationCommand implements Command {

   String project;

   String sandbox;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getSandboxServices()
         .getSandbox(user, project, sandbox)
         .thenApply(sandbox -> {
            var stacks = sandbox
               .getStacks()
               .stream()
               .map(DeployedStackDetails::getConfiguration)
               .collect(Collectors.toList());

            return Wrapped.apply(stacks);
         })
         .thenApply(DataResult::apply);
   }

   @Override
   public Command example() {
      return GetSandboxConfigurationCommand.apply("some-project", "some-sandbox");
   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   public static class Wrapped {

      List<StackConfiguration> stacks;

   }

}
