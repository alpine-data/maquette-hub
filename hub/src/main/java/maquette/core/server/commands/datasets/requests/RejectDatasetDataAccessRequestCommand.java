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
public class RejectDatasetDataAccessRequestCommand implements Command {

   String asset;

   UID id;

   String reason;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(asset) || asset.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`asset` must be supplied"));
      } else if (Objects.isNull(id)) {
         return CompletableFuture.failedFuture(new RuntimeException("`access-request-id` must be supplied"));
      }

      // TODO mw: Better validation process

      return services
         .getDatasetServices()
         .rejectDataAccessRequest(user, asset, id, reason)
         .thenApply(done -> MessageResult.apply("Successfully withdrawn data access request."));
   }

   @Override
   public Command example() {
      return RejectDatasetDataAccessRequestCommand.apply("my-funny-dataset", UID.apply(), "some justification");
   }
}
