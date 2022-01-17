package maquette.development.commands.members;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.authorization.Authorizations;
import maquette.core.values.authorization.GenericAuthorizationDefinition;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class RevokeWorkspaceMemberCommand implements Command {

    String name;

    GenericAuthorizationDefinition authorization;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        var auth = Authorizations.fromGenericAuthorizationDefinition(authorization);

        return runtime
            .getModule(MaquetteModelDevelopment.class)
            .getWorkspaceServices()
            .revoke(user, name, auth)
            .thenApply(done -> MessageResult.create("Revoked access from `%s`.", auth.getName()));
    }

    @Override
    public Command example() {
        return apply("some-dataset", GenericAuthorizationDefinition.apply("role", "a-team"));
    }

}
