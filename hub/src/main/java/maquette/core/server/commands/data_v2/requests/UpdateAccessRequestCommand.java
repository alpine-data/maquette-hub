package maquette.core.server.commands.data_v2.requests;

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
import maquette.core.values.UID;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class UpdateAccessRequestCommand implements Command {

   String name;

   UID id;

   String message;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDatasetServices()
         .updateDataAccessRequest(user, name, id, message)
         .thenApply(done -> MessageResult.apply("Successfully updated data access request."));
   }

   @Override
   public Command example() {
      return UpdateAccessRequestCommand.apply("some-dataset", UID.apply(), Operators.lorem());
   }
}
