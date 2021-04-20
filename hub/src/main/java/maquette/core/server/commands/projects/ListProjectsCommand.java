package maquette.core.server.commands.projects;

import lombok.AllArgsConstructor;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.TableResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class ListProjectsCommand implements Command {

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getProjectServices()
         .list(user)
         .thenApply(projects -> {
            var table = Table
               .create()
               .addColumns(StringColumn.create("name"))
               .addColumns(StringColumn.create("title"))
               .addColumns(DateTimeColumn.create("modified"))
               .addColumns(StringColumn.create("summary"))
               .addColumns(StringColumn.create("id"));

            projects.forEach(p -> {
               var row = table.appendRow();
               row.setString("name", p.getName());
               row.setString("title", p.getTitle());
               row.setDateTime("modified", LocalDateTime.ofInstant(p.getModified().getAt(), ZoneId.systemDefault()));
               row.setString("summary", p.getSummary());
               row.setString("id", p.getId().getValue());
            });

            return TableResult.apply(table.sortOn("name"), projects);
         });
   }

   @Override
   public Command example() {
      return apply();
   }

}
