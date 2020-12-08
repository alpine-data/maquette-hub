package maquette.core.entities.data.datasources;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.data.datasources.exceptions.DataSourceAlreadyExistsException;
import maquette.core.entities.data.datasources.exceptions.DataSourceNotFoundException;
import maquette.core.entities.data.datasources.model.DataSourceDatabaseProperties;
import maquette.core.entities.data.datasources.model.DataSourceProperties;
import maquette.core.entities.data.datasources.model.DataSourceType;
import maquette.core.ports.DataExplorer;
import maquette.core.ports.DataSourcesRepository;
import maquette.core.ports.RecordsStore;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;
import org.apache.commons.lang.NotImplementedException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataSourceEntities implements DataAssetEntities<DataSourceProperties, DataSourceEntity> {

   private final DataSourcesRepository repository;

   private final RecordsStore recordsStore;

   private final DataExplorer explorer;

   public CompletionStage<DataSourceProperties> createDataSource(
      User executor, String title, String name, String summary,
      DataSourceDatabaseProperties properties, DataSourceType type,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {

      return repository
         .findAssetByName(name)
         .thenCompose(maybeDataSource -> {
            if (maybeDataSource.isPresent()) {
               return CompletableFuture.failedFuture(DataSourceAlreadyExistsException.withName(name));
            } else {
               var created = ActionMetadata.apply("executor");
               var dataSource = DataSourceProperties.apply(
                  UID.apply(), title, name, summary,
                  properties, type,
                  visibility, classification, personalInformation, created, created);

               return repository
                  .insertOrUpdateAsset(dataSource)
                  .thenApply(d -> dataSource);
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
            DataSourceEntity.apply(properties.getId(), repository, recordsStore, explorer)));
   }

   public CompletionStage<Optional<DataSourceEntity>> findByName(String dataSource) {
      return repository
         .findAssetByName(dataSource)
         .thenApply(maybeDataSource -> maybeDataSource.map(properties ->
            DataSourceEntity.apply(properties.getId(), repository, recordsStore, explorer)));
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

}
