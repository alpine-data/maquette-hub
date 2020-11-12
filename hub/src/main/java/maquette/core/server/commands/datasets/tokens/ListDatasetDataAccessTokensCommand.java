package maquette.core.server.commands.datasets.tokens;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.TableResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ListDatasetDataAccessTokensCommand implements Command {


   String project;

   String dataset;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(project) || project.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`project` must be supplied"));
      } else if (Objects.isNull(dataset) || dataset.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`dataset` must be supplied"));
      }

      // TODO mw: Better validation process

      return services
         .getDatasetServices()
         .getDataAccessTokens(user, project, dataset)
         .thenApply(tokens -> {
            var table = Table
               .create()
               .addColumns(StringColumn.create("name"))
               .addColumns(StringColumn.create("key"));

            tokens.forEach(t -> {
               var row = table.appendRow();

               row.setString("name", t.getName());
               row.setString("key", t.getKey());
            });

            return TableResult.apply(table.sortOn("name"), tokens);
         });
   }

   @Override
   public Command example() {
      return ListDatasetDataAccessTokensCommand.apply("my-funny-project", "my-funny-dataset");
   }
}
