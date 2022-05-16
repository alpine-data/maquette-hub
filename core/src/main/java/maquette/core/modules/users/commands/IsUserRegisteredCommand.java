package maquette.core.modules.users.commands;

import maquette.core.MaquetteRuntime;
import maquette.core.modules.users.UserModule;
import maquette.core.server.commands.BooleanResult;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class IsUserRegisteredCommand implements Command {
    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        if (user instanceof AuthenticatedUser) {
            var sub = ((AuthenticatedUser) user).getId();
        }

        return runtime
            .getModule(UserModule.class)
            .getServices()
            .getProfileBySub(user)
            .thenApply(x -> {
                return BooleanResult.apply(x.getRegistered() != null ? x.getRegistered() : false);
            });
    }

    @Override
    public Command example() {
        return null;
    }
}
