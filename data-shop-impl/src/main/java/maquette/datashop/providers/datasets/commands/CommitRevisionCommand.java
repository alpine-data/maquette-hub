package maquette.datashop.providers.datasets.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.DataResult;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.providers.datasets.Datasets;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CommitRevisionCommand implements Command {

   String dataset;

   UID revision;

   String message;

   @Override
   public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
      return runtime
          .getModule(MaquetteDataShop.class)
          .getProviders()
          .getByType(Datasets.class)
          .getServices()
          .commit(user, dataset, revision, message)
          .thenApply(DataResult::apply);
   }

   @Override
   public Command example() {
      return apply("some-dataset", UID.apply(), Operators.lorem());
   }
}
