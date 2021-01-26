package maquette.core.server.commands.views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.DatasetView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.access.DataAccessRequestStatus;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class DatasetViewCommand implements Command {

   String dataset;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(dataset) || dataset.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`dataset` must be supplied"));
      }

      var datasetCS = services
         .getDatasetServices()
         .get(user, dataset);

      var logsCS = services
         .getDatasetServices()
         .getAccessLogs(user, dataset)
         .exceptionally(ex -> List.of());

      return Operators.compose(datasetCS, logsCS, (dataset, logs) -> {
         var isOwner = dataset.isMember(user, DataAssetMemberRole.OWNER);
         var isConsumer = dataset.isMember(user, DataAssetMemberRole.CONSUMER);
         var isProducer = dataset.isMember(user, DataAssetMemberRole.PRODUCER);
         var isMember = dataset.isMember(user, DataAssetMemberRole.MEMBER);

         var isSubscriber = dataset
            .getAccessRequests()
            .stream()
            .anyMatch(r -> r.getStatus().equals(DataAccessRequestStatus.GRANTED));

         var canAccessData = isOwner || isConsumer || isMember || isSubscriber;
         var canWriteData = isOwner || isProducer;

         return DatasetView.apply(dataset, logs, canAccessData, canWriteData, isOwner, isMember);
      });
   }

   @Override
   public Command example() {
      return apply("some-dataset");
   }

}
