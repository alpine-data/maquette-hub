package maquette.core.server.commands.datasets.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.UID;
import maquette.core.values.user.User;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class WithdrawDatasetDataAccessRequestCommand implements Command {

   String asset;

   UID id;

   String message;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(asset) || asset.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`asset` must be supplied"));
      } else if (Objects.isNull(id)) {
         return CompletableFuture.failedFuture(new RuntimeException("`id` must be supplied"));
      }

      return services
         .getDatasetServices()
         .withdrawDataAccessRequest(user, asset, id, message)
         .thenApply(done -> MessageResult.apply("Successfully withdrawn data access request."));
   }

   @Override
   public Command example() {
      return WithdrawDatasetDataAccessRequestCommand.apply("my-funny-dataset", UID.apply(), "some justification");
   }
}
