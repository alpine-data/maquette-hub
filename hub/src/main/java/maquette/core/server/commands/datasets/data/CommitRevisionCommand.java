package maquette.core.server.commands.datasets.data;

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
public class CommitRevisionCommand implements Command {

   String project;

   String dataset;

   String revision;

   String message;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(project) || project.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`project` must be supplied"));
      } else if (Objects.isNull(dataset) || dataset.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`dataset` must be supplied"));
      } else if (Objects.isNull(revision) || revision.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`revision` must be supplied"));
      } else if (Objects.isNull(message) || message.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`message` must be supplied"));
      }

      return services
         .getDatasetServices()
         .commitRevision(user, project, dataset, revision, message)
         .thenApply(DataResult::apply);
   }

   @Override
   public Command example() {
      // TODO: Fill with real schema example
      return CommitRevisionCommand.apply("my-funny-project", "Funny Dataset", "revision", "message");
   }
}
