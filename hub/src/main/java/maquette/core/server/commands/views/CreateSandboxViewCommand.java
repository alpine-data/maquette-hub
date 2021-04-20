package maquette.core.server.commands.views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.projects.model.sandboxes.Sandbox;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.CreateSandboxView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateSandboxViewCommand implements Command {

   String project;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      var projectCS = services
         .getProjectServices()
         .get(user, project);

      var stacksCS = services
         .getSandboxServices()
         .getStacks(user);

      var gitRepositoriesCS = services
         .getUserServices()
         .getGitRepositories(user);

      var volumesCS = projectCS.thenApply(project -> project
         .getSandboxes()
         .stream()
         .map(Sandbox::getVolume)
         .collect(Collectors.toList()));

      return Operators.compose(
         projectCS,
         stacksCS,
         gitRepositoriesCS,
         volumesCS,
         CreateSandboxView::apply);
   }

   @Override
   public Command example() {
      return new CreateSandboxViewCommand();
   }

}
