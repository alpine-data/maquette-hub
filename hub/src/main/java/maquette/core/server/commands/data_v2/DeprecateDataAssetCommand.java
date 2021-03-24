package maquette.core.server.commands.data_v2;

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
public class DeprecateDataAssetCommand implements Command {

   String name;

   boolean deprecate;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDataAssetServices()
         .deprecate(user, name, deprecate)
         .thenApply(done -> MessageResult.apply("Successfully deprecated asset"));
   }

   @Override
   public Command example() {
      return DeprecateDataAssetCommand.apply("some-dataset", true);
   }

}
