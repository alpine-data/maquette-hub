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
public class DatasetViewCommand implements Command, DataAssetViewCommandMixin {

   String name;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(name) || name.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`dataset` must be supplied"));
      }

      var datasetCS = services
         .getDatasetServices()
         .get(user, name);

      var logsCS = services
         .getCollectionServices()
         .getAccessLogs(user, name)
         .exceptionally(ex -> List.of());

      var ownersCS = datasetCS.thenCompose(dataset ->
         getUserProfiles(user, services, dataset, DataAssetMemberRole.OWNER));

      var stewardsCS = datasetCS.thenCompose(dataset ->
         getUserProfiles(user, services, dataset, DataAssetMemberRole.STEWARD));

      return Operators.compose(datasetCS, logsCS, ownersCS, stewardsCS, (dataset, logs, owners, stewards) -> {
         var permissions = dataset.getDataAssetPermissions(user);
         return DatasetView.apply(dataset, logs, permissions, owners, stewards);
      });
   }

   @Override
   public Command example() {
      return apply("some-dataset");
   }

}
