package maquette.asset_providers.sources;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.asset_providers.sources.model.DataSourceProperties;
import maquette.asset_providers.sources.model.DataSourceSettings;
import maquette.asset_providers.sources.ports.DataSourceDataExplorer;
import maquette.core.entities.data.DataAssetEntity;
import maquette.asset_providers.sources.model.ConnectionTestResult;
import maquette.asset_providers.sources.model.DataSourceDriver;
import maquette.asset_providers.sources.ports.JdbcPort;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.User;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataSourceEntities {

   private static final Logger LOG = LoggerFactory.getLogger(DataSources.class);

   private final JdbcPort jdbcPort;

   private final DataSourceDataExplorer dataExplorer;

   public CompletionStage<Done> analyze(DataAssetEntity entity) {
      return entity
         .getCustomSettings(DataSourceSettings.class)
         .thenCompose(settings -> download(settings)
            .exceptionally(e -> Records.empty())
            .thenApply(records -> DataSourceProperties.apply(records.size(), records.getSchema()))
            .thenCompose(entity::updateCustomProperties))
         .thenApply(done -> {
            entity
               .getProperties()
               .thenCompose(properties -> dataExplorer
                  .analyze(properties.getMetadata().getName())
                  .thenApply(result -> Pair.of(properties, result)))
               .thenCompose(pair -> entity
                  .readAndUpdateCustomProperties(DataSourceProperties.class, p -> p.withExplorer(pair.getRight()))
                  .thenApply(d -> pair.getLeft()))
               .thenApply(properties -> {
                  LOG.info("Successfully analyzed data source {}", properties.getMetadata().getName());
                  return Done.getInstance();
               });

            return done;
         });
   }

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
