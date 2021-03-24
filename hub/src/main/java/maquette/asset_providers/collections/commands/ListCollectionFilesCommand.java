package maquette.asset_providers.collections.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.asset_providers.collections.Collections;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.DataResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.Objects;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ListCollectionFilesCommand implements Command {

   String collection;

   String tag;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(tag)) {
         return runtime
            .getDataAssetProviders()
            .getByType(Collections.class)
            .getServices(runtime)
            .listFiles(user, collection)
            .thenApply(DataResult::apply);
      } else {
         return runtime
            .getDataAssetProviders()
            .getByType(Collections.class)
            .getServices(runtime)
            .listFiles(user, collection, tag)
            .thenApply(DataResult::apply);
      }
   }

   @Override
   public Command example() {
      return apply("some-collection", null);
   }

}
