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
public class TrackProductionByProjectCommand implements Command {

   String asset;

   String project;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDependencyServices()
         .trackProductionByProject(user, asset, project)
         .thenApply(done -> MessageResult.apply("Ok"));
   }

   @Override
   public Command example() {
      return TrackProductionByProjectCommand.apply(
         "some-dataset",
         "some-project");
   }
}
