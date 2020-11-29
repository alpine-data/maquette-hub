package maquette.core.server.commands.datasets.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.DataResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateDatasetDataAccessRequestCommand implements Command {

   String dataset;

   String origin;

   String reason;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(dataset) || dataset.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`dataset` must be supplied"));
      } else if (Objects.isNull(origin) || origin.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`origin` must be supplied"));
      } else if (Objects.isNull(reason) || reason.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`reason` must be supplied"));
      }

      return services
         .getDatasetServices()
         .createDataAccessRequest(user, dataset, origin, reason)
         .thenApply(DataResult::apply);
   }

   @Override
   public Command example() {
      return CreateDatasetDataAccessRequestCommand.apply("my-funny-dataset", "some-other-project", "Because he wants to.");
   }
}
