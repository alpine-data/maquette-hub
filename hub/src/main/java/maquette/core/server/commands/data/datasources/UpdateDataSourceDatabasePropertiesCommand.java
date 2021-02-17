package maquette.core.server.commands.data.datasources;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.datasources.model.DataSourceAccessType;
import maquette.core.entities.data.datasources.model.DataSourceDatabaseProperties;
import maquette.core.entities.data.datasources.model.DataSourceDriver;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class UpdateDataSourceDatabasePropertiesCommand implements Command {

   String source;

   DataSourceDatabaseProperties properties;

   DataSourceAccessType accessType;

   String schedule;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (properties == null) {
         throw new IllegalArgumentException("properties must not be null"); // TODO better error message (domain exception)
      }

      return services
         .getDataSourceServices()
         .updateDatabaseProperties(user, source, properties.getDriver(), properties.getConnection(), properties.getUsername(), properties.getPassword(), properties.getQuery(), accessType)
         .thenApply(done -> MessageResult.apply("Successfully updated data source."));
   }

   @Override
   public Command example() {
      return apply("some-data-source", DataSourceDatabaseProperties.apply(DataSourceDriver.POSTGRESQL, "//host/database", "SELECT * FROM TABLE", "egon", "secret123"), DataSourceAccessType.DIRECT, "daily");
   }

}
