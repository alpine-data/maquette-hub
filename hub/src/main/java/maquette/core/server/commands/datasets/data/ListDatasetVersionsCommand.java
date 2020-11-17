package maquette.core.server.commands.datasets.data;

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
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ListDatasetVersionsCommand implements Command {

   String project;

   String dataset;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(project) || project.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`project` must be supplied"));
      } else if (Objects.isNull(dataset) ||dataset.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`dataset` must be supplied"));
      }

      return services
         .getDatasetServices()
         .getVersions(user, project, dataset)
         .thenApply(revisions -> {
            var table = Table
               .create()
               .addColumns(StringColumn.create("version"))
               .addColumns(LongColumn.create("records"))
               .addColumns(DateTimeColumn.create("committed"));

            revisions.forEach(r -> {
               var row = table.appendRow();

               row.setString("version", r.getVersion().toString());
               row.setLong("records", r.getRecords());
               row.setDateTime("committed", LocalDateTime.ofInstant(r.getCommitted().getAt(), ZoneId.systemDefault()));
            });

            return TableResult.apply(table, revisions);
         });
   }

   @Override
   public Command example() {
      // TODO: Fill with real schema example
      return ListDatasetVersionsCommand.apply("my-funny-project", "Funny-dataset");
   }
}
