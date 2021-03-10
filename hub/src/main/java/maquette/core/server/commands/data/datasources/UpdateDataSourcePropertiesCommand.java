package maquette.core.server.commands.data.datasources;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.DataZone;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class UpdateDataSourcePropertiesCommand implements Command {

   String source;

   String name;

   String title;

   String summary;

   DataVisibility visibility;

   DataClassification classification;

   PersonalInformation personalInformation;

   DataZone zone;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDataSourceServices()
         .update(user, source, name, title, summary, visibility, classification, personalInformation, zone)
         .thenApply(done -> MessageResult.apply("Successfully updated data source."));
   }

   @Override
   public Command example() {
      return apply(
         "some-data-source", "some-data-source", "Some Data Source", Operators.lorem(),
         DataVisibility.PUBLIC, DataClassification.PUBLIC, PersonalInformation.NONE, DataZone.RAW);
   }

}
