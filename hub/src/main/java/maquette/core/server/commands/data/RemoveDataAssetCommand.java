package maquette.core.server.commands.data;

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
public class RemoveDataAssetCommand implements Command {

   String name;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDataAssetServices()
         .remove(user, name)
         .thenApply(pid -> MessageResult.apply("Successfully removed data asset `%s` and all related resources.", name));
   }

   @Override
   public Command example() {
      return RemoveDataAssetCommand.apply("some-dataset");
   }

}
