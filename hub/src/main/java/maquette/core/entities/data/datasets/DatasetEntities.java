package maquette.core.entities.data.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.data.datasets.exceptions.DatasetAlreadyExistsException;
import maquette.core.entities.data.datasets.exceptions.DatasetNotFoundException;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.ports.DataExplorer;
import maquette.core.ports.DatasetsRepository;
import maquette.core.ports.RecordsStore;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.data.*;
import maquette.core.values.user.User;
import org.apache.commons.lang.NotImplementedException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DatasetEntities implements DataAssetEntities<DatasetProperties, DatasetEntity> {

   private final DatasetsRepository repository;

   private final RecordsStore store;

   private final DataExplorer dataExplorer;

   public CompletionStage<DatasetProperties> create(
      User executor, String title, String name, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation,
      DataZone zone, DataAssetState state) {

      return repository
         .findAssetByName(name)
         .thenCompose(maybeDataset -> {
            if (maybeDataset.isPresent()) {
               return CompletableFuture.failedFuture(DatasetAlreadyExistsException.withName(name));
            } else {
               var created = ActionMetadata.apply(executor);
               var dataset = DatasetProperties.apply(
                  UID.apply(), title, name, summary,
                  visibility, classification, personalInformation, zone, state, created, created);

               return repository
                  .insertOrUpdateAsset(dataset)
                  .thenCompose(d -> getById(dataset.getId()))
                  .thenCompose(entity -> entity.getMembers().addMember(executor, executor.toAuthorization(), DataAssetMemberRole.OWNER))
                  .thenApply(d -> dataset);
            }
         });
   }

   public CompletionStage<Optional<DatasetEntity>> findById(UID dataset) {
      return repository
         .findAssetById(dataset)
         .thenApply(maybeDataset -> maybeDataset.map(details ->
            DatasetEntity.apply(details.getId(), repository, store, dataExplorer)));
   }

   public CompletionStage<Optional<DatasetEntity>> findByName(String dataset) {
      return repository
         .findAssetByName(dataset)
         .thenApply(maybeDataset -> maybeDataset.map(details ->
            DatasetEntity.apply(details.getId(), repository, store, dataExplorer)));
   }

   public CompletionStage<List<DatasetProperties>> list() {
      return repository.findAllAssets();
   }

   public CompletionStage<List<DataAccessRequestProperties>> findAccessRequestsByProject(UID project) {
      return repository.findDataAccessRequestsByProject(project);
   }

   public CompletionStage<DatasetEntity> getById(UID dataset) {
      return findById(dataset).thenApply(opt -> opt.orElseThrow(() -> DatasetNotFoundException.withId(dataset)));
   }

   public CompletionStage<DatasetEntity> getByName(String dataset) {
      return findByName(dataset).thenApply(opt -> opt.orElseThrow(() -> DatasetNotFoundException.withName(dataset)));
   }

   public CompletionStage<Done> remove(UID dataset) {
      throw new NotImplementedException(); // TODO
   }

}
