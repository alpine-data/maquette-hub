package maquette.core.server.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class UpdateDatasetDataAccessRequestCommand implements Command {


   String project;

   String dataset;

   String id;

   String message;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(project) || project.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`project` must be supplied"));
      } else if (Objects.isNull(dataset) || dataset.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`dataset` must be supplied"));
      } else if (Objects.isNull(id)) {
         return CompletableFuture.failedFuture(new RuntimeException("`id` must be supplied"));
      }

      // TODO mw: Better validation process

      return services
         .getDatasetServices()
         .updateDataAccessRequest(user, project, dataset, id, message)
         .thenApply(done -> MessageResult.apply("Successfully updated data access request."));
   }

   @Override
   public Command example() {
      return UpdateDatasetDataAccessRequestCommand.apply("my-funny-project", "my-funny-dataset", "user", "some justification");
   }
}
