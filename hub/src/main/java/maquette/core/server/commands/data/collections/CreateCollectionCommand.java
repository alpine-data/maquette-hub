package maquette.core.server.commands.data.collections;

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
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateCollectionCommand implements Command {

   String title;

   String name;

   String summary;

   DataVisibility visibility;

   DataClassification classification;

   PersonalInformation personalInformation;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getCollectionServices()
         .create(user, title, name, summary, visibility, classification, personalInformation)
         .thenApply(pid -> MessageResult.apply("Successfully created collection `%s`", name));
   }

   @Override
   public Command example() {
      return apply(
         "Some Collection", "some-collection", Operators.lorem(),
         DataVisibility.PUBLIC, DataClassification.PUBLIC, PersonalInformation.NONE);
   }

}
