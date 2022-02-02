package maquette.development.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.TableResult;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;
import maquette.development.values.EnvironmentType;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GetWorkspaceEnvironmentCommand implements Command {

    private static final String KEY = "key";
    private static final String VALUE = "value";

    String workspace;

    EnvironmentType type;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        var environmentType = type != null ? type : EnvironmentType.EXTERNAL;

        return runtime.getModule(MaquetteModelDevelopment.class)
            .getWorkspaceServices()
            .environment(user, workspace, environmentType)
            .thenApply(properties -> {
                var table = Table
                    .create()
                    .addColumns(StringColumn.create(KEY))
                    .addColumns(StringColumn.create(VALUE));

                properties.keySet().forEach(p -> {
                    var row = table.appendRow();
                    row.setString(KEY, p);
                    row.setString(VALUE, properties.get(p));
                });

                return TableResult.apply(table.sortOn(KEY), properties);
            });
    }

    @Override
    public Command example() {
        return apply("some-workspace", EnvironmentType.EXTERNAL);
    }

}
