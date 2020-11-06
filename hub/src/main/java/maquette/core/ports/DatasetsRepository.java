package maquette.core.ports;

import akka.Done;
import maquette.core.entities.datasets.model.DatasetProperties;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DatasetsRepository extends HasDataAccessRequests, HasDataAccessTokens, HasDataOwner {

   CompletionStage<List<DatasetProperties>> findAllDatasets();

   CompletionStage<List<DatasetProperties>> findAllDatasets(String projectId);

   CompletionStage<Optional<DatasetProperties>> findDatasetById(String projectId, String datasetId);

   CompletionStage<Optional<DatasetProperties>> findDatasetByName(String projectId, String datasetName);

   CompletionStage<Done> insertOrUpdateDataset(String projectId, DatasetProperties dataset);

}
