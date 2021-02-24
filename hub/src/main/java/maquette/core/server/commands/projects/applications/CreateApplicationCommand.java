package maquette.core.server.commands.projects.applications;

import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class CreateApplicationCommand implements Command {

   String project;

   String name;

   String description;

   String gitRepository;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getProjectServices()
         .createApplication(user, project, name, description, gitRepository)
         .thenApply(done -> MessageResult.apply("Application created."));
   }

   @Override
   public Command example() {
      return apply("some-project", "some-app", Operators.lorem(), Operators.lorem());
   }

}
