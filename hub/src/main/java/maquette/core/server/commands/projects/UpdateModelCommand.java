package maquette.core.server.commands.projects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class UpdateModelCommand implements Command {

   String project;

   String model;

   String title;

   String description;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getProjectServices()
         .updateModel(user, project, model, title, description)
         .thenApply(pid -> MessageResult.apply("Successfully updated model"));
   }

   @Override
   public Command example() {
      return apply("some-project", "Some Project", "some-model", Operators.lorem());
   }

}
