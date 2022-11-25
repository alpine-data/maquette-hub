package maquette.core.modules.users.commands;

import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.users.UserModule;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.DataResult;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class GetGlobalRoleCommand implements Command {

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(UserModule.class)
            .getServices()
            .getGlobalRolesForUser(user)
            .thenApply(DataResult::apply);
    }

    @Override
    public Command example() {
        return GetGlobalRoleCommand.apply();
    }

}
