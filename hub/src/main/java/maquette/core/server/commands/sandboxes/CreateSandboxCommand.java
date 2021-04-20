package maquette.core.server.commands.sandboxes;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.projects.model.sandboxes.stacks.PostgreSqlStack;
import maquette.core.entities.projects.model.sandboxes.stacks.PythonStack;
import maquette.core.entities.projects.model.sandboxes.stacks.StackConfiguration;
import maquette.core.entities.projects.model.sandboxes.volumes.PlainVolume;
import maquette.core.entities.projects.model.sandboxes.volumes.VolumeDefinition;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.DataResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateSandboxCommand implements Command {

   String project;

   String name;

   VolumeDefinition volume;

   List<StackConfiguration> stacks;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getSandboxServices()
         .createSandbox(user, project, name, volume, stacks)
         .thenApply(DataResult::apply);
   }

   @Override
   public Command example() {
      return apply(
         "some-project", "some-sandbox",
         PlainVolume.apply("some-volume"),
         List.of(
            PythonStack.Configuration.apply("3.8"),
            PostgreSqlStack.Configuration.apply("user", "password", "admin@maquette.ai", "password")
         ));
   }

}
