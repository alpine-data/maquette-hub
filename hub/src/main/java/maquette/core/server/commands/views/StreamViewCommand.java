package maquette.core.server.commands.views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.DatasetView;
import maquette.core.server.views.StreamView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.access.DataAccessRequestStatus;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.user.User;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class StreamViewCommand implements Command {

   String stream;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getStreamServices()
         .get(user, stream)
         .thenApply(stream -> {
            var isOwner = stream.isMember(user, DataAssetMemberRole.OWNER);
            var isConsumer = stream.isMember(user, DataAssetMemberRole.CONSUMER);
            var isMember = stream.isMember(user, DataAssetMemberRole.MEMBER);

            var isSubscriber = stream
               .getAccessRequests()
               .stream()
               .anyMatch(r -> r.getStatus().equals(DataAccessRequestStatus.GRANTED));

            var canAccessData = isOwner || isConsumer || isMember || isSubscriber;

            return StreamView.apply(stream, canAccessData, isOwner, isMember);
         });
   }

   @Override
   public Command example() {
      return apply("some-stream");
   }

}
