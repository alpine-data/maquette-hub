package maquette.core.entities.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.datasets.model.DatasetDetails;
import maquette.core.ports.DatasetsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class Datasets {

   private final DatasetsRepository repository;

   public CompletionStage<DatasetDetails> createDataset(User executor, String projectId, String name, String summary, String description) {
      var created = ActionMetadata.apply(executor);
      var dataset = DatasetDetails.apply(UUID.randomUUID().toString(), name, summary, description, created, created);
      return repository
         .insertOrUpdateDataset(projectId, dataset)
         .thenApply(d -> dataset);
   }

   public CompletionStage<Optional<Dataset>> findDatasetById(String projectId, String datasetId) {
      return repository
         .findDatasetById(projectId, datasetId)
         .thenApply(maybeDataset -> maybeDataset.map(details -> Dataset.apply(details.getId(), projectId, details.getName(), repository)));
   }

   public CompletionStage<Optional<Dataset>> findDatasetByName(String projectId, String datasetName) {
      return repository
         .findDatasetByName(projectId, datasetName)
         .thenApply(maybeDataset -> maybeDataset.map(details -> Dataset.apply(details.getId(), projectId, details.getName(), repository)));
   }

   public CompletionStage<List<DatasetDetails>> findDatasets(String projectId) {
      return repository.findAllDatasets(projectId);
   }

   public CompletionStage<Done> removeDataset(String projectId, String datasetId) {
      // TODO implement
      return CompletableFuture.completedFuture(Done.getInstance());
   }

}
