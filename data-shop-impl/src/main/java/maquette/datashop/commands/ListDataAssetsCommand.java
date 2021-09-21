package maquette.datashop.commands;

import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.TableResult;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class ListDataAssetsCommand implements Command {

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {

        return runtime
            .getModule(MaquetteDataShop.class)
            .getServices()
            .list(user)
            .thenApply(datasets -> {
                var table = Table
                    .create()
                    .addColumns(StringColumn.create("type"))
                    .addColumns(StringColumn.create("name"))
                    .addColumns(StringColumn.create("visibility"))
                    .addColumns(StringColumn.create("classification"))
                    .addColumns(StringColumn.create("personal information"));

                datasets.forEach(p -> {
                    var row = table.appendRow();
                    row.setString("type", p.getType());
                    row.setString("name", p.getMetadata().getName());
                    row.setString("visibility", p.getMetadata().getVisibility().getValue());
                    row.setString("classification", p.getMetadata().getClassification().getValue());
                    row.setString("personal information", p.getMetadata().getPersonalInformation().getValue());
                });

                return TableResult.apply(table.sortDescendingOn("name"), datasets);
            });
    }

    @Override
    public Command example() {
        return ListDataAssetsCommand.apply();
    }

}
