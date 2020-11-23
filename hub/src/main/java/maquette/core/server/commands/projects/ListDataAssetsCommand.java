package maquette.core.server.commands.projects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.TableResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.user.User;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ListDataAssetsCommand implements Command {

   String project;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(project) || project.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`project` must be supplied"));
      }

      return services
         .getProjectServices()
         .getDataAssets(user, project)
         .thenApply(assets -> {
            // TODO mw: Better CSV content
            var table = Table
               .create()
               .addColumns(StringColumn.create("id"))
               .addColumns(StringColumn.create("title"))
               .addColumns(StringColumn.create("name"))
               .addColumns(DateTimeColumn.create("modified"));

            assets.forEach(p -> {
               var row = table.appendRow();
               row.setString("id", p.getId());
               row.setString("title", p.getTitle());
               row.setString("name", p.getName());
               row.setDateTime("modified", LocalDateTime.ofInstant(p.getUpdated().getAt(), ZoneId.systemDefault()));
            });

            var wrapper = new DataAssetsList();
            wrapper.addAll(assets);

            return TableResult.apply(table, wrapper);
         });
   }

   @Override
   public Command example() {
      return ListDataAssetsCommand.apply("some-project");
   }

   /*
    * Workaround for Jackson de-serialization issue of generic class' lists.
    * https://github.com/FasterXML/jackson-databind/pull/1309
    */
   public static class DataAssetsList extends ArrayList<DataAssetProperties> {

   }

}
