package maquette.core.server.commands.data.datasources;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.datasources.model.DataSourceDatabaseProperties;
import maquette.core.entities.data.datasources.model.DataSourceDriver;
import maquette.core.entities.data.datasources.model.DataSourceType;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateDataSourceCommand implements Command {

   String title;

   String name;

   String summary;

   DataSourceDatabaseProperties properties;

   DataSourceType type;

   DataVisibility visibility;

   DataClassification classification;

   PersonalInformation personalInformation;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDataSourceServices()
         .create(user, title, name, summary, properties, type, visibility, classification, personalInformation)
         .thenApply(pid -> MessageResult.apply("Successfully created data source `%s`", name));
   }

   @Override
   public Command example() {
      return apply(
         "Some Data Source", "some-data-source", Operators.lorem(),
         DataSourceDatabaseProperties.apply(DataSourceDriver.POSTGRESQL, "jdbc://h2:7291", "SELECT * FROM TABLE"), DataSourceType.DIRECT,
         DataVisibility.PUBLIC, DataClassification.PUBLIC, PersonalInformation.NONE);
   }

}
