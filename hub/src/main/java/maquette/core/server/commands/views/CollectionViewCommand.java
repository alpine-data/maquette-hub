package maquette.core.server.commands.views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.CollectionView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.access.DataAccessRequestStatus;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CollectionViewCommand implements Command {

   String collection;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getCollectionServices()
         .get(user, collection)
         .thenApply(collection -> {
            var isOwner = collection.isMember(user, DataAssetMemberRole.OWNER);
            var isConsumer = collection.isMember(user, DataAssetMemberRole.CONSUMER);
            var isMember = collection.isMember(user, DataAssetMemberRole.MEMBER);

            var isSubscriber = collection
               .getAccessRequests()
               .stream()
               .anyMatch(r -> r.getStatus().equals(DataAccessRequestStatus.GRANTED));

            var canAccessData = isOwner || isConsumer || isMember || isSubscriber;

            return CollectionView.apply(collection, canAccessData, isOwner, isMember);
         });
   }

   @Override
   public Command example() {
      return apply("some-collection");
   }

}
