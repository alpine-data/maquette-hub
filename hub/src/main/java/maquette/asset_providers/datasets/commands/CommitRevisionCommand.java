package maquette.asset_providers.datasets.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.asset_providers.datasets.Datasets;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.DataResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.UID;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CommitRevisionCommand implements Command {

   String dataset;

   UID revision;

   String message;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return runtime
         .getDataAssetProviders()
         .getByType(Datasets.class)
         .getServices(runtime)
         .commit(user, dataset, revision, message)
         .thenApply(DataResult::apply);
   }

   @Override
   public Command example() {
      return apply("some-dataset", UID.apply(), Operators.lorem());
   }
}