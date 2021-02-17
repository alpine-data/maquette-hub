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
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class DataSourceViewCommand implements Command, DataAssetViewCommandMixin {

   String name;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      var sourceCS = services
         .getDataSourceServices()
         .get(user, name);

      var logsCS = services
         .getDatasetServices()
         .getAccessLogs(user, name)
         .exceptionally(ex -> List.of());

      var ownersCS = sourceCS.thenCompose(source ->
         getUserProfiles(user, services, source, DataAssetMemberRole.OWNER));

      var stewardsCS = sourceCS.thenCompose(source ->
         getUserProfiles(user, services, source, DataAssetMemberRole.STEWARD));

      return Operators.compose(sourceCS, logsCS, ownersCS, stewardsCS, (source, logs, owners, stewards) -> {
         var permissions = source.getDataAssetPermissions(user);
         return DataSourceView.apply(source, logs, permissions, owners, stewards);
      });
   }

   @Override
   public Command example() {
      return apply("some-source");
   }

}
