package maquette.core.server.commands.datasets;

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

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class RevokeDatasetAccessCommand implements Command {

   String dataset;

   Authorization authorization;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDatasetServices()
         .revokeDatasetMember(user,dataset, authorization)
         .thenApply(done -> MessageResult.apply("Successfully revoked ownership."));
   }

   @Override
   public Command example() {
      return apply("some-dataset", RoleAuthorization.apply("A_TEAM"));
   }

}
