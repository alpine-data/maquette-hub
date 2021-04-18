package maquette.core.server.commands.projects;

import lombok.AllArgsConstructor;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.TableResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class ListWorkspaceGeneratorsCommand implements Command {

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getProjectServices()
         .listWorkspaceGenerators(user)
         .thenApply(templates -> {
            var table = Table
               .create()
               .addColumns(StringColumn.create("name"))
               .addColumns(StringColumn.create("description"));

            templates.forEach(t -> {
               var row = table.appendRow();
               row.setString("name", t.getName());
               row.setString("description", t.getDescription() != null ? t.getDescription() : "<no description>");
            });

            return TableResult.apply(table.sortOn("name"), templates);
         });
   }

   @Override
   public Command example() {
      return apply();
   }

}
