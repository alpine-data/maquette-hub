package maquette.core.modules.users.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.modules.users.UserModule;
import maquette.core.modules.users.model.UserAuthenticationToken;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class UserInformationCommand implements Command {

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(UserModule.class)
            .getServices()
            .getAuthenticationToken(user)
            .thenApply(token -> {
                var info = UserInformation.apply(user, token);
                var json = Operators.suppressExceptions(() -> runtime.getObjectMapperFactory().createJsonMapper(true).writeValueAsString(info));
                return MessageResult.create(json, info);
            });
    }

    @Override
    public Command example() {
        return UserInformationCommand.apply();
    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class UserInformation {

        User user;

        UserAuthenticationToken token;

    }

}
