package maquette.core.server.commands.views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.DataSourceView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.access.DataAccessRequestStatus;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class DataSourceViewCommand implements Command {

   String source;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      var sourceCS = services
         .getDataSourceServices()
         .get(user, source);

      var logsCS = services
         .getDatasetServices()
         .getAccessLogs(user, source)
         .exceptionally(ex -> List.of());

      return Operators.compose(sourceCS, logsCS, (source, logs) -> {
         var isOwner = source.isMember(user, DataAssetMemberRole.OWNER);
         var isConsumer = source.isMember(user, DataAssetMemberRole.CONSUMER);
         var isMember = source.isMember(user, DataAssetMemberRole.MEMBER);

         var isSubscriber = source
            .getAccessRequests()
            .stream()
            .anyMatch(r -> r.getStatus().equals(DataAccessRequestStatus.GRANTED));

         var canAccessData = isOwner || isConsumer || isMember || isSubscriber;

         return DataSourceView.apply(source, logs, canAccessData, isOwner, isMember);
      });
   }

   @Override
   public Command example() {
      return apply("some-source");
   }

}
