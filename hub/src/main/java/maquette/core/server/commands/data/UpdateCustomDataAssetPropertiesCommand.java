package maquette.core.server.commands.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class UpdateCustomDataAssetPropertiesCommand implements Command {

   String name;

   Object customProperties;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDataAssetServices()
         .updateCustomProperties(user, name, customProperties)
         .thenApply(done -> MessageResult.apply("Successfully updated data asset."));
   }

   @Override
   public Command example() {
      var meta = new Object();
      return apply("some-dataset", meta);
   }

}
