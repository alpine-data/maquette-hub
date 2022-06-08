package maquette.core.modules.users.commands;

import maquette.core.MaquetteRuntime;
import maquette.core.modules.users.UserModule;
import maquette.core.server.commands.BooleanResult;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

public class IsUserRegisteredCommand implements Command {
    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {


        return runtime
            .getModule(UserModule.class)
            .getServices()
            .getProfile(user)
            .thenApply(x -> {
                return BooleanResult.apply(x.getRegistered() != null ? x.getRegistered() : false);
            });
    }

    @Override
    public Command example() {
        return null;
    }
}
