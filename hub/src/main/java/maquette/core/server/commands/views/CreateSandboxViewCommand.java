package maquette.core.server.commands.views;

import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.CreateSandboxView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

public final class CreateSandboxViewCommand implements Command {

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      var projectsCS = services
         .getUserServices()
         .getProjects(user);

      var stacksCS = services
         .getSandboxServices()
         .getStacks(user);

      return Operators.compose(projectsCS, stacksCS, CreateSandboxView::apply);
   }

   @Override
   public Command example() {
      return null;
   }

}
