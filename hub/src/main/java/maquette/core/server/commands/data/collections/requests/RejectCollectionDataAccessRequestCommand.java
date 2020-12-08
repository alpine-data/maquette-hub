package maquette.core.server.commands.data.collections.requests;

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
public class RejectCollectionDataAccessRequestCommand implements Command {

   String asset;

   UID id;

   String reason;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getCollectionServices()
         .rejectDataAccessRequest(user, asset, id, reason)
         .thenApply(done -> MessageResult.apply("Successfully withdrawn data access request."));
   }

   @Override
   public Command example() {
      return RejectCollectionDataAccessRequestCommand.apply("some-collection", UID.apply(), Operators.lorem());
   }
}
