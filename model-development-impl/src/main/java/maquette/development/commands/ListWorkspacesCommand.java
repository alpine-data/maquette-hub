package maquette.development.commands;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.TableResult;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
public class ListWorkspacesCommand implements Command {

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime.getModule(MaquetteModelDevelopment.class)
            .getServices()
            .list(user)
            .thenApply(workspaces -> {
                var table = Table
                    .create()
                    .addColumns(StringColumn.create("name"))
                    .addColumns(StringColumn.create("title"))
                    .addColumns(DateTimeColumn.create("modified"))
                    .addColumns(StringColumn.create("summary"))
                    .addColumns(StringColumn.create("id"));

                workspaces.forEach(p -> {
                    var row = table.appendRow();
                    row.setString("name", p.getName());
                    row.setString("title", p.getTitle());
                    row.setDateTime("modified", LocalDateTime.ofInstant(p.getModified().getAt(), ZoneId.systemDefault()));
                    row.setString("summary", p.getSummary());
                    row.setString("id", p.getId().getValue());
                });

                return TableResult.apply(table.sortOn("name"), workspaces);
            });
    }

    @Override
    public Command example() {
        return apply();
    }

}
