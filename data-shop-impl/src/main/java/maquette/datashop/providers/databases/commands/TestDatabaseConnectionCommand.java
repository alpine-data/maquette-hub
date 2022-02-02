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

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class TestDatabaseConnectionCommand implements Command {

    DatabaseDriver driver;

    String connection;

    String username;

    String password;

    String query;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteDataShop.class)
            .getProviders()
            .getByType(Databases.class)
            .getServices()
            .test(driver, connection, username, password, query)
            .thenApply(DataResult::apply);
    }

    @Override
    public Command example() {
        return apply(DatabaseDriver.POSTGRESQL, "foo/bar", "some-user", "some-password", "query");
    }

}
