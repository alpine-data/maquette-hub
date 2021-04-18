package maquette.asset_providers.sources.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.asset_providers.datasets.model.CommittedRevision;
import maquette.asset_providers.datasets.model.DatasetVersion;
import maquette.asset_providers.sources.model.ConnectionTestResult;
import maquette.asset_providers.sources.model.DataSourceDriver;
import maquette.common.Operators;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.data.DataAssetEntity;
import maquette.core.entities.logs.Action;
import maquette.core.entities.logs.ActionCategory;
import maquette.core.entities.logs.Logs;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataSourceServicesLogged implements DataSourceServices {

   private final DataSourceServices delegate;

   private final DataAssetEntities entities;

   private final Logs logs;

   private final DataAssetCompanion assets;

   @Override
   public CompletionStage<Done> analyze(User executor, String source) {
      return delegate.analyze(executor, source);
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataSource) {
      var ridCS = entities.getByName(dataSource).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.download(executor, dataSource);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         var action = Action.apply(
            ActionCategory.READ, "Fetched data from `%s`", dataSource);

         logs.log(executor, action, rid);
         assets.trackConsumption(executor, dataSource);

         return result;
      });
   }

   @Override
   public CompletionStage<ConnectionTestResult> test(DataSourceDriver driver, String connection, String username, String password, String query) {
      return delegate.test(driver, connection, username, password, query);
   }

}
