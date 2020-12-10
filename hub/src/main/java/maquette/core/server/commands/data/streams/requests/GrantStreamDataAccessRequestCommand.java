package maquette.core.server.commands.data.streams.requests;

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

import java.time.Instant;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GrantStreamDataAccessRequestCommand implements Command {

   String asset;

   UID id;

   Instant until;

   String message;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getStreamServices()
         .grantDataAccessRequest(user, asset, id, until, message)
         .thenApply(done -> MessageResult.apply("Data Access Request has been granted successfully"));
   }

   @Override
   public Command example() {
      return apply("some-stream", UID.apply(), Instant.now(), Operators.lorem());
   }
}
