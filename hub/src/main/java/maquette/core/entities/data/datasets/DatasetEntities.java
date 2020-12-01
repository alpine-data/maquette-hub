package maquette.core.entities.data.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.data.datasets.exceptions.DatasetAlreadyExistsException;
import maquette.core.entities.data.datasets.exceptions.DatasetNotFoundException;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.ports.DatasetsRepository;
import maquette.core.ports.DatasetsStore;
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
public final class DatasetEntities {

   private final DatasetsRepository repository;

   private final DatasetsStore store;

   public CompletionStage<DatasetProperties> createDataset(
      User executor, String title, String name, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {

      return repository
         .findDatasetByName(name)
         .thenCompose(maybeDataset -> {
            if (maybeDataset.isPresent()) {
               return CompletableFuture.failedFuture(DatasetAlreadyExistsException.withName(name));
            } else {
               var created = ActionMetadata.apply(executor);
               var dataset = DatasetProperties.apply(
                  UID.apply(), title, name, summary,
                  visibility, classification, personalInformation, created, created);

               return repository
                  .insertOrUpdateDataset(dataset)
                  .thenApply(d -> dataset);
            }
         });
   }

   public CompletionStage<Optional<DatasetEntity>> findDatasetById(UID dataset) {
      return repository
         .findDatasetById(dataset)
         .thenApply(maybeDataset -> maybeDataset.map(details -> DatasetEntity.apply(details.getId(), repository, store)));
   }

   public CompletionStage<Optional<DatasetEntity>> findDatasetByName(String dataset) {
      return repository
         .findDatasetByName(dataset)
         .thenApply(maybeDataset -> maybeDataset.map(details -> DatasetEntity.apply(details.getId(), repository, store)));
   }

   public CompletionStage<List<DatasetProperties>> findDatasets() {
      return repository.findAllDatasets();
   }

   public CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByProject(UID project) {
      return repository.findDataAccessRequestsByProject(project);
   }

   public CompletionStage<DatasetEntity> getDatasetById(UID dataset) {
      return findDatasetById(dataset).thenApply(opt -> opt.orElseThrow(() -> DatasetNotFoundException.withId(dataset)));
   }

   public CompletionStage<DatasetEntity> getDatasetByName(String dataset) {
      return findDatasetByName(dataset).thenApply(opt -> opt.orElseThrow(() -> DatasetNotFoundException.withName(dataset)));
   }

   public CompletionStage<Done> removeDataset(UID dataset) {
      throw new NotImplementedException(); // TODO
   }

}