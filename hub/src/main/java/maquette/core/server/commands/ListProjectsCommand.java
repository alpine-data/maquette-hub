package maquette.core.server.commands;

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
                            .addColumns(StringColumn.create("name"));

                    projects.forEach(p -> {
                        var row = table.appendRow();
                        row.setString("id", p.getId());
                        row.setString("name", p.getName());
                    });

                   return TableResult.apply(table.sortOn("name"), projects);
                });
    }

   @Override
   public Command example() {
      return ListProjectsCommand.apply();
   }

}
