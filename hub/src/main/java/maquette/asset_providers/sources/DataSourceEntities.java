package maquette.asset_providers.sources;

import lombok.AllArgsConstructor;
import maquette.asset_providers.sources.model.DataSourceSettings;
import maquette.core.entities.data.DataAssetEntity;
import maquette.asset_providers.sources.model.ConnectionTestResult;
import maquette.asset_providers.sources.model.DataSourceDriver;
import maquette.asset_providers.sources.ports.JdbcPort;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataSourceEntities {

   private final JdbcPort jdbcPort;

   public CompletionStage<Records> download(DataAssetEntity entity, User executor) {
      return entity
         .getCustomSettings(DataSourceSettings.class)
         .thenCompose(this::download);
   }

   public CompletionStage<Records> download(DataSourceSettings properties) {
      return jdbcPort.read(
         properties.getDriver(), properties.getConnection(),
         properties.getUsername(), properties.getPassword(), properties.getQuery());
   }

   public CompletionStage<ConnectionTestResult> test(
      DataSourceDriver driver, String connection, String username, String password, String query) {

      return jdbcPort.test(driver, connection, username, password, query);
   }

   public CompletionStage<ConnectionTestResult> test(DataSourceSettings properties) {
      return jdbcPort.test(properties.getDriver(), properties.getConnection(), properties.getUsername(), properties.getPassword(), properties.getQuery());
   }

}
