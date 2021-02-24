package maquette.core.server.commands.projects.applications;

import lombok.AllArgsConstructor;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class RemoveApplicationCommand implements Command {

   String project;

   String name;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getProjectServices()
         .removeApplication(user, project, name)
         .thenApply(done -> MessageResult.apply("Application removed."));
   }

   @Override
   public Command example() {
      return apply("some-project", "some-app");
   }

}
