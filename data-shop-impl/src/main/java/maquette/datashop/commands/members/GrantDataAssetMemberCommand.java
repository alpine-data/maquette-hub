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
import maquette.datashop.values.access.DataAssetMemberRole;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class GrantDataAssetMemberCommand implements Command {

    String name;

    GenericAuthorizationDefinition authorization;

    DataAssetMemberRole role;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteDataShop.class)
            .getServices()
            .grant(user, name, Authorizations.fromGenericAuthorizationDefinition(authorization), role)
            .thenApply(done -> MessageResult.create("Successfully granted ownership."));
    }

    @Override
    public Command example() {
        return apply("some-dataset", GenericAuthorizationDefinition.apply("user", "edgar"), DataAssetMemberRole.OWNER);
    }

}
