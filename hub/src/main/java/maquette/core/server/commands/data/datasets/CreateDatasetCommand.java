package maquette.core.server.commands.data.datasets;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.DataZone;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
public class CreateDatasetCommand implements Command {

   String title;

   String name;

   String summary;

   DataVisibility visibility;

   DataClassification classification;

   PersonalInformation personalInformation;

   DataZone zone;

   String owner;

   String steward;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDatasetServices()
         .create(
            user, title, name, summary, visibility, classification, personalInformation, zone,
            UserAuthorization.apply(owner), UserAuthorization.apply(steward))
         .thenApply(pid -> MessageResult.apply("Successfully created dataset `%s`", name));
   }

   @Override
   public Command example() {
      return apply(
         "Some Dataset", "some-dataset", Operators.lorem(),
         DataVisibility.PUBLIC, DataClassification.PUBLIC, PersonalInformation.NONE, DataZone.RAW, "alice", "bob");
   }

}
