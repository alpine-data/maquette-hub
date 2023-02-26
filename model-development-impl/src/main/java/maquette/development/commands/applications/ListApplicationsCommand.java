package maquette.development.commands.applications;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.TableResult;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.concurrent.CompletionStage;


@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ListApplicationsCommand implements Command {

    String workspace;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteModelDevelopment.class)
            .getWorkspaceServices()
            .findApplicationsInWorkspace(runtime, user, workspace)
            .thenApply(applications -> {
                var table = Table
                    .create()
                    .addColumns(StringColumn.create("id"))
                    .addColumns(StringColumn.create("name"))
                    .addColumns(StringColumn.create("metaInfo"));

                applications.forEach(p -> {
                    var row = table.appendRow();
                    row.setString("id", p.getId().getValue());
                    row.setString("name", p.getName());
                    row.setString("metaInfo", p.getMetaInfo());
                });

                return TableResult.apply(table.sortDescendingOn("name"), applications);
            });
    }


    @Override
    public Command example() {
        return apply("workspace-1");
    }
}
