package maquette.core.server.commands.data.requests;

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
public class GrantAccessRequestCommand implements Command {

   String name;

   UID id;

   Instant until;

   String message;

   String environment;

   boolean downstreamApprovalRequired;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDataAssetServices()
         .grantDataAccessRequest(user, name, id, until, message, environment, downstreamApprovalRequired)
         .thenApply(done -> MessageResult.apply("Data Access Request has been granted successfully"));
   }

   @Override
   public Command example() {
      return apply("some-dataset", UID.apply(), Instant.now(), Operators.lorem(), "any", true);
   }
}
