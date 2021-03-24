package maquette.asset_providers.sources.services;

import lombok.AllArgsConstructor;
import maquette.asset_providers.sources.DataSourceEntities;
import maquette.asset_providers.sources.DataSources;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.datasources.model.ConnectionTestResult;
import maquette.core.entities.data.datasources.model.DataSourceDriver;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataSourceServicesImpl implements DataSourceServices {

   private final RuntimeConfiguration configuration;

   private final DataSourceEntities entities;

   @Override
   public CompletionStage<Records> download(User executor, String source) {
      return configuration
         .getDataAssets()
         .getByName(source, DataSources.TYPE_NAME)
         .thenCompose(entity -> entities.download(entity, executor));
   }

   @Override
   public CompletionStage<ConnectionTestResult> test(DataSourceDriver driver, String connection, String username, String password, String query) {
      return entities.test(driver, connection, username, password, query);
   }

}
