package maquette.core.server.commands.views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.StreamView;
import maquette.core.services.ApplicationServices;

import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class StreamViewCommand implements Command, DataAssetViewCommandMixin {

   String name;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      var streamCS = services
         .getStreamServices()
         .get(user, name);

      var logsCS = services
         .getDatasetServices()
         .getAccessLogs(user, name)
         .exceptionally(ex -> List.of());

      var ownersCS = streamCS.thenCompose(stream ->
         getUserProfiles(user, services, stream, DataAssetMemberRole.OWNER));

      var stewardsCS = streamCS.thenCompose(stream ->
         getUserProfiles(user, services, stream, DataAssetMemberRole.STEWARD));

      return Operators.compose(streamCS, logsCS, ownersCS, stewardsCS, (stream, logs, owners, stewards) -> {
         var permissions = stream.getDataAssetPermissions(user);
         return StreamView.apply(stream, logs, permissions, owners, stewards);
      });
   }

   @Override
   public Command example() {
      return apply("some-stream");
   }

}
