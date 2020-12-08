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
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class GrantStreamMemberCommand implements Command {

   String stream;

   Authorization authorization;

   DataAssetMemberRole role;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getStreamServices()
         .grant(user, stream, authorization, role)
         .thenApply(done -> MessageResult.apply("Successfully granted ownership."));
   }

   @Override
   public Command example() {
      return apply("some-stream", UserAuthorization.apply("edgar"), DataAssetMemberRole.OWNER);
   }

}
