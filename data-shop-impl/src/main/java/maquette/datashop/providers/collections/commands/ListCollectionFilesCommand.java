package maquette.datashop.providers.collections.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.DataResult;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.providers.collections.Collections;

import java.util.Objects;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ListCollectionFilesCommand implements Command {

   String collection;

   String tag;

   @Override
   public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
      return runtime
         .getModule(MaquetteDataShop.class)
         .getProviders()
         .getByType(Collections.class)
         .getServices()
         .listFiles(user, collection, tag)
         .thenApply(DataResult::apply);
   }

   @Override
   public Command example() {
      return apply("some-collection", null);
   }

}
