package maquette.core.server.commands.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.collections.CollectionProperties;
import maquette.core.entities.data.streams.StreamProperties;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.DataShopView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;
import org.apache.commons.compress.utils.Lists;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataShopViewCommand implements Command {

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      var allDatasetsCS = services
         .getDatasetServices()
         .getDatasets(user)
         .thenApply(list -> list
            .stream()
            .map(p -> (DataAssetProperties) p)
            .collect(Collectors.toList()));

      var userDatasetsCS = services
         .getUserServices()
         .getDataAssets(user);

      return Operators.compose(allDatasetsCS, userDatasetsCS, (allAssets, userAssets) -> {
         StreamProperties stream = StreamProperties.apply(UID.apply(), "Stock Prices", "stock-prices", "Continuously updated stock prices from common exchanges.", "", DataVisibility.PUBLIC, DataClassification.INTERNAL, PersonalInformation.NONE, ActionMetadata.apply(user), ActionMetadata.apply(user));
         CollectionProperties collection = CollectionProperties.apply(UID.apply(), "Customer Reviews", "customer-reviews", "Collected customer reviews to analyze sentiment of our clients.", "", DataVisibility.PUBLIC, DataClassification.CONFIDENTIAL, PersonalInformation.PERSONAL_INFORMATION, ActionMetadata.apply(user), ActionMetadata.apply(user));

         List<DataAssetProperties> all = Lists.newArrayList();
         all.addAll(allAssets);
         all.add(stream);
         all.add(collection);
         all = all.stream().sorted(Comparator.comparing(DataAssetProperties::getName)).collect(Collectors.toList());

         return DataShopView.apply(userAssets, all);
      });
   }

   @Override
   public Command example() {
      return null;
   }

}
