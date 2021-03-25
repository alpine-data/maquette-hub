package maquette.core.server.commands.views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.model.DataAsset;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.DataAssetView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class DataAssetViewCommand implements Command {

   String name;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      var assetCS = services
         .getDataAssetServices()
         .get(user, name);

      var logsCS = services
         .getDataAssetServices()
         .getAccessLogs(user, name)
         .exceptionally(ex -> List.of());

      var ownersCS = assetCS.thenCompose(collection ->
         getUserProfiles(user, services, collection, DataAssetMemberRole.OWNER));

      var stewardsCS = assetCS.thenCompose(collection ->
         getUserProfiles(user, services, collection, DataAssetMemberRole.STEWARD));

      var graphCS = services
         .getDependencyServices()
         .getDependencyGraph(user, name);

      return Operators.compose(assetCS, logsCS, ownersCS, stewardsCS, graphCS, (collection, logs, owners, stewards, graph) -> {
         var permissions = collection.getDataAssetPermissions(user);
         return DataAssetView.apply(collection, logs, permissions, owners, stewards, graph);
      });
   }

   private CompletionStage<List<UserProfile>> getUserProfiles(
      User user, ApplicationServices services, DataAsset asset, DataAssetMemberRole role) {

      return Operators.allOf(asset.getMembers(role)
         .stream()
         .map(GrantedAuthorization::getAuthorization)
         .filter(auth -> auth instanceof UserAuthorization)
         .map(auth -> (UserAuthorization) auth)
         .map(m -> services.getUserServices().getProfile(user, m.getName())));
   }

   @Override
   public Command example() {
      return apply("some-asset");
   }

}
