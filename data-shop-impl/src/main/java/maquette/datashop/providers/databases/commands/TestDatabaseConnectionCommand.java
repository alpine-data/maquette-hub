package maquette.datashop.providers.databases.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.DataResult;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.providers.databases.Databases;
import maquette.datashop.providers.databases.model.DatabaseDriver;
import maquette.datashop.providers.databases.model.DatabaseQuerySettings;
import maquette.datashop.providers.databases.model.DatabaseSessionSettings;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class TestDatabaseConnectionCommand implements Command {

    DatabaseSessionSettings sessionSettings;

    List<DatabaseQuerySettings> queries;

    boolean allowCustomQueries;

    boolean allowLocalSession;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteDataShop.class)
            .getProviders()
            .getByType(Databases.class)
            .getServices()
            .test(sessionSettings.getDriver(), sessionSettings.getConnection(), sessionSettings.getUsername(),
                sessionSettings.getPassword(), queries
                    .get(0)
                    .getQuery())
            .thenApply(DataResult::apply);
    }

    @Override
    public Command example() {
        return apply(DatabaseSessionSettings.apply(DatabaseDriver.POSTGRESQL, "foo/bar", "some-user", "some-password"),
            List.of(DatabaseQuerySettings.apply("name", "query")), false, false);
    }

}
