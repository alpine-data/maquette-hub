package maquette.core.entities.data.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.ports.DatasetsRepository;
import maquette.core.ports.DatasetsStore;
import maquette.core.values.ActionMetadata;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class Datasets {

   private final DatasetsRepository repository;

   private final DatasetsStore store;

   public CompletionStage<DatasetProperties> createDataset(
      User executor, String projectId,
      String title, String name, String summary, String description,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {

      var created = ActionMetadata.apply(executor);
      var dataset = DatasetProperties.apply(
         Operators.hash(), title, name, summary, description,
         visibility, classification, personalInformation, created, created);

      return repository
         .insertOrUpdateDataset(projectId, dataset)
         .thenApply(d -> dataset);
   }

   public CompletionStage<Optional<Dataset>> findDatasetById(String projectId, String datasetId) {
      return repository
         .findDatasetById(projectId, datasetId)
         .thenApply(maybeDataset -> maybeDataset.map(details -> Dataset.apply(details.getId(), projectId, details.getName(), repository, store)));
   }

   public CompletionStage<Optional<Dataset>> findDatasetByName(String projectId, String datasetName) {
      return repository
         .findDatasetByName(projectId, datasetName)
         .thenApply(maybeDataset -> maybeDataset.map(details -> Dataset.apply(details.getId(), projectId, details.getName(), repository, store)));
   }

   public CompletionStage<List<DatasetProperties>> findDatasets(String projectId) {
      return repository.findAllDatasets(projectId);
   }

   public CompletionStage<List<DataAccessRequest>> findDataAccessRequestsByOrigin(String originProjectId) {
      return repository.findDataAccessRequestsByOrigin(originProjectId);
   }

   public CompletionStage<Dataset> getDatasetById(String projectId, String datasetId) {
      return findDatasetById(projectId, datasetId).thenApply(Optional::orElseThrow); // TODO mw: Better exception Handling
   }

   public CompletionStage<Done> removeDataset(String projectId, String datasetId) {
      // TODO implement
      return CompletableFuture.completedFuture(Done.getInstance());
   }

}
