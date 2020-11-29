package maquette.core.server.commands.datasets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.user.User;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class GrantDatasetAccessCommand implements Command {

   String dataset;

   String name;

   DataAssetMemberRole role;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(dataset) || dataset.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`dataset` must be supplied"));
      } else if (Objects.isNull(name) || name.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`name` must be supplied"));
      }

      var auth = UserAuthorization.apply(name);

      return services
         .getDatasetServices()
         .grantDatasetMember(user, dataset, auth, role)
         .thenApply(done -> MessageResult.apply("Successfully granted ownership."));
   }

   @Override
   public Command example() {
      return apply("user", "edgar", DataAssetMemberRole.OWNER);
   }

}
