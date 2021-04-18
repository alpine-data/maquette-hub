package maquette.asset_providers.sources.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.asset_providers.sources.DataSources;
import maquette.asset_providers.sources.model.DataSourceDriver;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.DataResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class AnalyzeDataSourceCommand implements Command {

   String name;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return runtime
         .getDataAssetProviders()
         .getByType(DataSources.class)
         .getServices(runtime)
         .analyze(user, name)
         .thenApply(DataResult::apply);
   }

   @Override
   public Command example() {
      return apply("some-source");
   }

}
