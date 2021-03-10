package maquette.core.entities.data.datasources;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.data.assets.DataAssetEntities;
import maquette.core.entities.data.datasources.exceptions.DataSourceAlreadyExistsException;
import maquette.core.entities.data.datasources.exceptions.DataSourceFetchException;
import maquette.core.entities.data.datasources.exceptions.DataSourceNotFoundException;
import maquette.core.entities.data.datasources.model.*;
import maquette.core.ports.DataExplorer;
import maquette.core.ports.DataSourcesRepository;
import maquette.core.ports.JdbcPort;
import maquette.core.ports.RecordsStore;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.data.*;
import maquette.core.values.user.User;
import org.apache.commons.lang.NotImplementedException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataSourceEntities implements DataAssetEntities<DataSourceProperties, DataSourceEntity> {

   private final DataSourcesRepository repository;

   private final JdbcPort jdbcPort;

   private final RecordsStore recordsStore;

   private final DataExplorer explorer;

   public CompletionStage<DataSourceProperties> create(
      User executor, String title, String name, String summary,
      DataSourceDatabaseProperties properties, DataSourceAccessType type,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation,
      DataZone zone) {

      return repository
         .findAssetByName(name)
         .thenCompose(maybeDataSource -> {
            if (maybeDataSource.isPresent()) {
               return CompletableFuture.failedFuture(DataSourceAlreadyExistsException.withName(name));
            } else {
               return jdbcPort
                  .test(properties)
                  .thenCompose(result -> {
                     if (result instanceof FailedConnectionTestResult) {
                        return CompletableFuture.failedFuture(DataSourceFetchException.apply((FailedConnectionTestResult) result));
                     } else if (result instanceof SuccessfulConnectionTestResult) {
                        var schema = ((SuccessfulConnectionTestResult) result).getSchema();
                        var records = ((SuccessfulConnectionTestResult) result).getRecords();
                        var fetched = Instant.now();

                        var created = ActionMetadata.apply(executor);
                        var dataSource = DataSourceProperties.apply(
                           UID.apply(), title, name, summary,
                           properties, type,
                           visibility, classification, personalInformation,
                           zone, DataAssetState.APPROVED,
                           schema, fetched, records, created, created);

                        return repository
                           .insertOrUpdateAsset(dataSource)
                           .thenCompose(d -> getById(dataSource.getId()))
                           .thenCompose(e -> e.getMembers().addMember(executor, executor.toAuthorization(), DataAssetMemberRole.OWNER))
                           .thenApply(d -> dataSource);
                     } else {
                        return CompletableFuture.failedFuture(new RuntimeException("unknown result type"));
                     }
                  });
            }
         });
   }

   @Override
   public CompletionStage<List<DataAccessRequestProperties>> findAccessRequestsByProject(UID project) {
      return repository.findDataAccessRequestsByProject(project);
   }

   public CompletionStage<Optional<DataSourceEntity>> findById(UID dataSource) {
      return repository
         .findAssetById(dataSource)
         .thenApply(maybeDataSource -> maybeDataSource.map(properties ->
            DataSourceEntity.apply(properties.getId(), repository, jdbcPort, recordsStore, explorer)));
   }

   public CompletionStage<Optional<DataSourceEntity>> findByName(String dataSource) {
      return repository
         .findAssetByName(dataSource)
         .thenApply(maybeDataSource -> maybeDataSource.map(properties ->
            DataSourceEntity.apply(properties.getId(), repository, jdbcPort, recordsStore, explorer)));
   }

   public CompletionStage<DataSourceEntity> getById(UID dataSource) {
      return findById(dataSource).thenApply(opt -> opt.orElseThrow(() -> DataSourceNotFoundException.withId(dataSource)));
   }

   public CompletionStage<DataSourceEntity> getByName(String dataSource) {
      return findByName(dataSource).thenApply(opt -> opt.orElseThrow(() -> DataSourceNotFoundException.withName(dataSource)));
   }

   public CompletionStage<List<DataSourceProperties>> list() {
      return repository.findAllAssets();
   }

   public CompletionStage<Done> remove(UID dataSource) {
      throw new NotImplementedException();
   }

   @Override
   public CompletionStage<UID> getResourceUID(String asset) {
      return getByName(asset).thenApply(DataSourceEntity::getId).thenApply(this::getResourceUID);
   }

   @Override
   public UID getResourceUID(UID asset) {
      return asset.withParent("sources");
   }

   public CompletionStage<ConnectionTestResult> test(DataSourceDriver driver, String connection, String username, String password, String query) {
      return jdbcPort.test(driver, connection, username, password, query);
   }

}
