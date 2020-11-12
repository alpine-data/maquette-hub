package maquette.core.server.commands.datasets.requests;

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
public class ListDatasetDataAccessRequestsCommand implements Command {


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
         .getDataAccessRequests(user, project, dataset)
         .thenApply(requests -> {
            var table = Table
               .create()
               .addColumns(StringColumn.create("id"))
               .addColumns(StringColumn.create("origin"))
               .addColumns(StringColumn.create("created by"))
               .addColumns(StringColumn.create("created"))
               .addColumns(StringColumn.create("status"));

            requests.forEach(r -> {
               var row = table.appendRow();

               row.setString("id", r.getId());
               row.setString("origin", r.getOrigin().getName());
               row.setString("created by", r.getCreated().getBy());
               row.setString("created", r.getCreated().getAt().toString()); // TODO format date time
               row.setString("status", r.getStatus().name());
            });

            return TableResult.apply(table, requests);
         });
   }

   @Override
   public Command example() {
      return ListDatasetDataAccessRequestsCommand.apply("my-funny-project", "my-funny-dataset");
   }
}
