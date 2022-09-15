package maquette.datashop.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.TableResult;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import javax.annotation.Nullable;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class QueryDataAssetsCommand implements Command {

    @Nullable
    String query;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {

        Operators.suppressExceptions(() -> Thread.sleep(1000));

        return runtime
            .getModule(MaquetteDataShop.class)
            .getServices()
            .query(user, query)
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
                    row.setString("name", p
                        .getMetadata()
                        .getName());
                    row.setString("visibility", p
                        .getMetadata()
                        .getVisibility()
                        .getValue());
                    row.setString("classification", p
                        .getMetadata()
                        .getClassification()
                        .getValue());
                    row.setString("personal information", p
                        .getMetadata()
                        .getPersonalInformation()
                        .getValue());
                });

                return TableResult.apply(table.sortDescendingOn("name"), datasets);
            });
    }

    @Override
    public Command example() {
        return QueryDataAssetsCommand.apply("some query");
    }

}
