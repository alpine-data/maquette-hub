package maquette.core.modules.users.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.users.GlobalRole;
import maquette.core.modules.users.UserModule;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.authorization.Authorizations;
import maquette.core.values.authorization.GenericAuthorizationDefinition;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class GrantGlobalRoleCommand implements Command {

    GenericAuthorizationDefinition authorization;

    GlobalRole role;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(UserModule.class)
            .getServices()
            .grantGlobalRole(user, Authorizations.fromGenericAuthorizationDefinition(authorization), role)
            .thenApply(done -> MessageResult.create("Successfully granted global role assignment."));
    }

    @Override
    public Command example() {
        return GrantGlobalRoleCommand.apply(GenericAuthorizationDefinition.apply("user", "egon"), GlobalRole.ADVANCED_USER);
    }

}
