package maquette.datashop.commands;

import com.google.common.collect.Sets;
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
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.commands.views.DataAssetView;
import maquette.datashop.values.DataAsset;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.access_requests.DataAccessRequestUserTriggeredEvent;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        var usersCS = assetCS.thenCompose(asset -> {
            var accessRequestCreatorsNames = asset.getAccessRequests()
                .stream()
                .map(request -> request.getCreated().getBy())
                .collect(Collectors.toList());

            var accessRequestActorsNames = asset.getAccessRequests()
                .stream()
                .flatMap(req -> req.getEvents()
                    .stream()
                    .filter(e -> e instanceof DataAccessRequestUserTriggeredEvent)
                    .map(e -> ((DataAccessRequestUserTriggeredEvent) e).getCreated().getBy()))
                .collect(Collectors.toList());

            var membersNames = asset.getMembers()
                .stream()
                .map(GrantedAuthorization::getAuthorization)
                .filter(auth -> auth instanceof UserAuthorization)
                .map(Authorization::getName)
                .collect(Collectors.toList());

            var allUserNames = Sets.<String>newHashSet();
            allUserNames.addAll(accessRequestCreatorsNames);
            allUserNames.addAll(accessRequestActorsNames);
            allUserNames.addAll(membersNames);

            return Operators
                .allOf(allUserNames
                    .stream()
                    .map(m -> runtime.getModule(UserModule.class).getServices().getProfile(user, UID.apply(m))))
                .thenApply(list -> list
                    .stream()
                    .collect(Collectors.toMap((UserProfile p) -> p.getId().getValue(), Function.identity())));
        });

        return Operators.compose(assetCS, usersCS, (asset, users) -> {
            var permissions = asset.getDataAssetPermissions(user);

            var owners = asset.getMembers().stream()
                .filter(auth -> auth.getRole().equals(DataAssetMemberRole.OWNER))
                .filter(auth -> users.containsKey(auth.getAuthorization().getName()))
                .map(auth -> users.get(auth.getAuthorization().getName()))
                .collect(Collectors.toList());

            var stewards = asset.getMembers().stream()
                .filter(auth -> auth.getRole().equals(DataAssetMemberRole.STEWARD))
                .filter(auth -> users.containsKey(auth.getAuthorization().getName()))
                .map(auth -> users.get(auth.getAuthorization().getName()))
                .collect(Collectors.toList());

            return DataAssetView.apply(asset, permissions, owners, stewards, users);
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
