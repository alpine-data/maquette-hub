package maquette.datashop.commands.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class UpdateAccessRequestCommand implements Command {

   String name;

   UID id;

   String message;

   @Override
   public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
      return runtime
         .getModule(MaquetteDataShop.class)
         .getServices()
         .updateDataAccessRequest(user, name, id, message)
         .thenApply(done -> MessageResult.apply("Successfully updated data access request."));
   }

   @Override
   public Command example() {
      return UpdateAccessRequestCommand.apply("some-dataset", UID.apply(), Operators.lorem());
   }
}
