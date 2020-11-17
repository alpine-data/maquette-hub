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
import org.apache.avro.Schema;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateRevisionCommand implements Command {

   String project;

   String dataset;

   Schema schema;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(project) || project.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`project` must be supplied"));
      } else if (Objects.isNull(dataset) ||dataset.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`dataset` must be supplied"));
      } else if (Objects.isNull(schema)) {
         return CompletableFuture.failedFuture(new RuntimeException("`schema` must be supplied"));
      }

      return services
         .getDatasetServices()
         .createRevision(user, project, dataset, schema)
         .thenApply(DataResult::apply);
   }

   @Override
   public Command example() {
      // TODO: Fill with real schema example
      return CreateRevisionCommand.apply("my-funny-project", "Funny Dataset", null);
   }
}
