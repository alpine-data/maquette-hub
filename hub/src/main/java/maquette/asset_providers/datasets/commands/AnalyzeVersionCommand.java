package maquette.asset_providers.datasets.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.asset_providers.datasets.Datasets;
import maquette.asset_providers.datasets.model.DatasetVersion;
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
public class AnalyzeVersionCommand implements Command {

   String name;

   DatasetVersion version;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return runtime
         .getDataAssetProviders()
         .getByType(Datasets.class)
         .getServices(runtime)
         .analyze(user, name, version)
         .thenApply(done -> MessageResult.apply("ok"));
   }

   @Override
   public Command example() {
      return apply("some-dataset", DatasetVersion.apply("1.0.0"));
   }
}
