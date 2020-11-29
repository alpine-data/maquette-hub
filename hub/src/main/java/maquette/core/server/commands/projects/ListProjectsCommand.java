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
               .addColumns(StringColumn.create("id"))
               .addColumns(StringColumn.create("title"))
               .addColumns(StringColumn.create("name"))
               .addColumns(DateTimeColumn.create("modified"));

            projects.forEach(p -> {
               var row = table.appendRow();
               row.setString("id", p.getId().getValue());
               row.setString("title", p.getTitle());
               row.setString("name", p.getName());
               row.setDateTime("modified", LocalDateTime.ofInstant(p.getModified().getAt(), ZoneId.systemDefault()));
            });

            return TableResult.apply(table.sortOn("name"), projects);
         });
   }

   @Override
   public Command example() {
      return ListProjectsCommand.apply();
   }

}
