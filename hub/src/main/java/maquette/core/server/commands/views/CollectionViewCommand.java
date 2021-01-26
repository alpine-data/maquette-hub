package maquette.core.server.commands.views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
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
      var collectionCS = services
         .getCollectionServices()
         .get(user, collection);

      var logsCS = services
         .getCollectionServices()
         .getAccessLogs(user, collection);

      return Operators.compose(collectionCS, logsCS, (collection, logs) -> {
         var isOwner = collection.isMember(user, DataAssetMemberRole.OWNER);
         var isConsumer = collection.isMember(user, DataAssetMemberRole.CONSUMER);
         var isProducer = collection.isMember(user, DataAssetMemberRole.PRODUCER);
         var isMember = collection.isMember(user, DataAssetMemberRole.MEMBER);

         var isSubscriber = collection
            .getAccessRequests()
            .stream()
            .anyMatch(r -> r.getStatus().equals(DataAccessRequestStatus.GRANTED));

         var canAccessData = isOwner || isConsumer || isMember || isSubscriber;
         var canProduceData = isOwner || isProducer;

         return CollectionView.apply(collection, logs, canAccessData, canProduceData, isOwner, isMember);
      });
   }

   @Override
   public Command example() {
      return apply("some-collection");
   }

}
