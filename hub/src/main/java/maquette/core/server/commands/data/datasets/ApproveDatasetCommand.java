package maquette.core.server.commands.data.datasets;

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
public class ApproveDatasetCommand implements Command {

   String dataset;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDatasetServices()
         .approve(user, dataset)
         .thenApply(done -> MessageResult.apply("Successfully approved"));
   }

   @Override
   public Command example() {
      return ApproveDatasetCommand.apply("some-dataset");
   }

}
