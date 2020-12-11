package maquette.core.server.commands.views;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.DataShopView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.user.User;

import java.util.Comparator;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataShopViewCommand implements Command {

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      var allCollectionsCS = services
         .getCollectionServices()
         .list(user)
         .thenApply(list -> list
            .stream()
            .map(p -> (DataAssetProperties<?>) p)
            .collect(Collectors.toList()));

      var allDatasetsCS = services
         .getDatasetServices()
         .list(user)
         .thenApply(list -> list
            .stream()
            .map(p -> (DataAssetProperties<?>) p)
            .collect(Collectors.toList()));

      var allDataSourcesCS = services
         .getDataSourceServices()
         .list(user)
         .thenApply(list -> list
            .stream()
            .map(p -> (DataAssetProperties<?>) p)
            .collect(Collectors.toList()));

      var allStreamsCS = services
         .getStreamServices()
         .list(user)
         .thenApply(list -> list
            .stream()
            .map(p -> (DataAssetProperties<?>) p)
            .collect(Collectors.toList()));

      var allAssetsCS = Operators
         .compose(allCollectionsCS, allDatasetsCS, allDataSourcesCS, allStreamsCS, (collections, datasets, sources, streams) -> {
            var all = Lists.<DataAssetProperties<?>>newArrayList();
            all.addAll(collections);
            all.addAll(datasets);
            all.addAll(sources);
            all.addAll(streams);

            return all
               .stream()
               .sorted(Comparator.comparing(DataAssetProperties::getName))
               .collect(Collectors.toList());
         });

      var userDatasetsCS = services
         .getUserServices()
         .getDataAssets(user);

      return Operators.compose(allAssetsCS, userDatasetsCS, (allAssets, userAssets) -> DataShopView
         .apply(userAssets, allAssets.stream().map(a -> (DataAssetProperties<?>) a).collect(Collectors.toList())));
   }

   @Override
   public Command example() {
      return apply();
   }

}
