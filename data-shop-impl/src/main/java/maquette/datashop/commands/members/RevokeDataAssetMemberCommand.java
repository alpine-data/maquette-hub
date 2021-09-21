package maquette.datashop.commands.members;

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
import maquette.datashop.MaquetteDataShop;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class RevokeDataAssetMemberCommand implements Command {

    String name;

    GenericAuthorizationDefinition authorization;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteDataShop.class)
            .getServices()
            .revoke(user, name, Authorizations.fromGenericAuthorizationDefinition(authorization))
            .thenApply(done -> MessageResult.create("Successfully revoked ownership."));
    }

    @Override
    public Command example() {
        return apply("some-dataset", GenericAuthorizationDefinition.apply("role", "a-team"));
    }

}
