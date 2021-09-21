package maquette.datashop.providers.datasets.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.TableResult;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.providers.datasets.Datasets;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ListVersionsCommand implements Command {

    String dataset;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteDataShop.class)
            .getProviders()
            .getByType(Datasets.class)
            .getServices()
            .listCommits(user, dataset)
            .thenApply(revisions -> {
                var table = Table
                    .create()
                    .addColumns(StringColumn.create("type"))
                    .addColumns(StringColumn.create("name"))
                    .addColumns(StringColumn.create("visibility"))
                    .addColumns(StringColumn.create("classification"))
                    .addColumns(StringColumn.create("personal information"));

                revisions.forEach(r -> {
                    var row = table.appendRow();

                    row.setString("version", r.getVersion().toString());
                    row.setString("created by", r.getCreated().getBy());
                    row.setDateTime("created at", LocalDateTime
                        .ofInstant(r.getCreated().getAt(), ZoneOffset.systemDefault()));
                    row.setLong("record", r.getRecords());
                });

                return TableResult.apply(table.sortOn("version"), revisions);
            });
    }

    @Override
    public Command example() {
        return apply("some-dataset");
    }

}
