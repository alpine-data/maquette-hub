package maquette.asset_providers.collections.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.asset_providers.collections.Collections;
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
public class CreateCollectionTagCommand implements Command {

   String collection;

   String tag;

   String message;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return runtime
         .getDataAssetProviders()
         .getByType(Collections.class)
         .getServices(runtime)
         .tag(user, collection, tag, message)
         .thenApply(pid -> MessageResult.apply("Successfully created tag `%s` on collection.", tag, collection));
   }

   @Override
   public Command example() {
      return apply("some-collection", "tag", Operators.lorem());
   }

}
