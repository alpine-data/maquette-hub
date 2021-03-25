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
public class TrackConsumptionByModelCommand implements Command {

   String asset;

   String project;

   String model;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDependencyServices()
         .trackConsumptionByModel(user, asset, project, model)
         .thenApply(done -> MessageResult.apply("Ok"));
   }

   @Override
   public Command example() {
      return TrackConsumptionByModelCommand.apply(
         "some-dataset",
         "some-project",
         "soma-model");
   }
}
