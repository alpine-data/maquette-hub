package maquette.core.server.commands.dependencies;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
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
public class TrackConsumptionByApplicationCommand implements Command {

   String asset;

   String project;

   String application;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDependencyServices()
         .trackConsumptionByApplication(user, asset, project, application)
         .thenApply(done -> MessageResult.apply("Ok"));
   }

   @Override
   public Command example() {
      return TrackConsumptionByApplicationCommand.apply(
         "some-dataset",
         "some-project",
         "soma-app");
   }
}
