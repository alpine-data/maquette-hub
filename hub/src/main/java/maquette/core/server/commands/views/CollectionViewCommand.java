package maquette.core.server.commands.views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.CollectionView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.data.DataAsset;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CollectionViewCommand implements Command {

   String name;

   private CompletionStage<List<UserProfile>> getUserProfiles(
      User user, ApplicationServices services, DataAsset<?> asset, DataAssetMemberRole role) {

      return Operators.allOf(asset.getMembers(role)
         .stream()
         .map(GrantedAuthorization::getAuthorization)
         .filter(auth -> auth instanceof UserAuthorization)
         .map(auth -> (UserAuthorization) auth)
         .map(m -> services.getUserServices().getProfile(user, m.getName())));
   }

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      var collectionCS = services
         .getCollectionServices()
         .get(user, name);

      var logsCS = services
         .getCollectionServices()
         .getAccessLogs(user, name)
         .exceptionally(ex -> List.of());

      var ownersCS = collectionCS.thenCompose(collection ->
         getUserProfiles(user, services, collection, DataAssetMemberRole.OWNER));

      var stewardsCS = collectionCS.thenCompose(collection ->
         getUserProfiles(user, services, collection, DataAssetMemberRole.STEWARD));

      return Operators.compose(collectionCS, logsCS, ownersCS, stewardsCS, (collection, logs, owners, stewards) -> {
         var permissions = collection.getDataAssetPermissions(user);
         return CollectionView.apply(collection, logs, permissions, owners, stewards);
      });
   }

   @Override
   public Command example() {
      return apply("some-collection");
   }

}
