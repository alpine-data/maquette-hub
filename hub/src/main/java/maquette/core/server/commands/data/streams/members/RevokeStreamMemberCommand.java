package maquette.core.server.commands.data.streams.members;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.RoleAuthorization;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class RevokeStreamMemberCommand implements Command {

   String stream;

   Authorization authorization;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getStreamServices()
         .revoke(user, stream, authorization)
         .thenApply(done -> MessageResult.apply("Successfully revoked ownership."));
   }

   @Override
   public Command example() {
      return apply("some-stream", RoleAuthorization.apply("A_TEAM"));
   }

}
