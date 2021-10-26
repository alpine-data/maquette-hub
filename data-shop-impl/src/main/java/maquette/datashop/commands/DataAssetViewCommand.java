package maquette.datashop.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.modules.users.UserModule;
import maquette.core.modules.users.model.UserProfile;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.values.UID;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.commands.views.DataAssetView;
import maquette.datashop.values.DataAsset;
import maquette.datashop.values.access.DataAssetMemberRole;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class DataAssetViewCommand implements Command {

    String name;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        var services = runtime
            .getModule(MaquetteDataShop.class)
            .getServices();

        var assetCS = services
            .get(user, name);

        var ownersCS = assetCS.thenCompose(asset ->
            getUserProfiles(user, runtime, asset, DataAssetMemberRole.OWNER));

        var stewardsCS = assetCS.thenCompose(asset ->
            getUserProfiles(user, runtime, asset, DataAssetMemberRole.STEWARD));

        return Operators.compose(assetCS, ownersCS, stewardsCS, (asset, owners, stewards) -> {
            var permissions = asset.getDataAssetPermissions(user);
            return DataAssetView.apply(asset, permissions, owners, stewards);
        });
    }

    private CompletionStage<List<UserProfile>> getUserProfiles(
        User user, MaquetteRuntime runtime, DataAsset asset, DataAssetMemberRole role) {

        return Operators.allOf(asset.getMembers(role)
            .stream()
            .map(GrantedAuthorization::getAuthorization)
            .filter(auth -> auth instanceof UserAuthorization)
            .map(auth -> (UserAuthorization) auth)
            .map(m -> runtime.getModule(UserModule.class).getServices().getProfile(user, UID.apply(m.getName()))));
    }

    @Override
    public Command example() {
        return DataAssetViewCommand.apply("some-dataset");
    }

}
