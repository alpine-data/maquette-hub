package maquette.development.commands;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.TableResult;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;
import maquette.development.values.WorkspaceProperties;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.time.Instant;
import java.util.Comparator;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(staticName = "apply")
public class ListWorkspacesCommand implements Command {

    private static final String NAME = "workspace";
    private static final String TITLE = "title";
    private static final String MODIFIED = "modified";
    private static final String SUMMARY = "summary";
    private static final String ID = "workspace id";

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteModelDevelopment.class)
            .getWorkspaceServices()
            .list(user)
            .thenApply(workspaces -> workspaces
                .stream()
                .sorted(Comparator
                    .<WorkspaceProperties, Instant>comparing(wks -> wks
                        .getModified()
                        .getAt())
                    .reversed())
                .collect(Collectors.toList()))
            .thenApply(workspaces -> {
                var table = Table
                    .create()
                    .addColumns(StringColumn.create(ID))
                    .addColumns(StringColumn.create(NAME))
                    .addColumns(StringColumn.create(TITLE))
                    .addColumns(StringColumn.create(MODIFIED))
                    .addColumns(StringColumn.create(SUMMARY));

                workspaces.forEach(p -> {
                    var row = table.appendRow();
                    row.setString(NAME, p.getName());
                    row.setString(TITLE, p.getTitle());
                    row.setString(MODIFIED, Operators.toRelativeTimeString(p
                        .getModified()
                        .getAt()));
                    row.setString(SUMMARY, p.getSummary());
                    row.setString(ID, p
                        .getId()
                        .getValue());
                });

                return TableResult.apply(table.sortOn(NAME), workspaces);
            });
    }

    @Override
    public Command example() {
        return apply();
    }

}
