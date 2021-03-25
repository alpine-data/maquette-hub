package maquette.asset_providers.sources.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.asset_providers.sources.DataSources;
import maquette.core.config.RuntimeConfiguration;
import maquette.asset_providers.sources.model.DataSourceDriver;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.DataResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class TestDataSourceCommand implements Command {

   DataSourceDriver driver;

   String connection;

   String query;

   String username;

   String password;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return runtime
         .getDataAssetProviders()
         .getByType(DataSources.class)
         .getServices(runtime)
         .test(driver, connection, username, password, query)
         .thenApply(DataResult::apply);
   }

   @Override
   public Command example() {
      return apply(DataSourceDriver.POSTGRESQL, "host:5432", "SELECT * FROM TABLE", "edgar", "secret123");
   }

}
