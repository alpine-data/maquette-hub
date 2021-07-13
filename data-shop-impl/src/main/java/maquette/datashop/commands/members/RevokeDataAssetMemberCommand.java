package maquette.datashop.commands.members;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.RoleAuthorization;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class RevokeDataAssetMemberCommand implements Command {

   String name;

   Authorization authorization;

   @Override
   public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
      return runtime
         .getModule(MaquetteDataShop.class)
         .getServices()
         .revoke(user, name, authorization)
         .thenApply(done -> MessageResult.apply("Successfully revoked ownership."));
   }

   @Override
   public Command example() {
      return apply("some-dataset", RoleAuthorization.apply("A_TEAM"));
   }

}
