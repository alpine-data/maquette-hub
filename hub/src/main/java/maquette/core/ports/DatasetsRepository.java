package maquette.core.ports;

import akka.Done;
import maquette.core.entities.datasets.model.DatasetDetails;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DatasetsRepository extends HasDataAccessRequests, HasDataAccessTokens {

   CompletionStage<List<DatasetDetails>> findAllDatasets();

   CompletionStage<List<DatasetDetails>> findAllDatasets(String projectId);

   CompletionStage<Optional<DatasetDetails>> findDatasetById(String projectId, String datasetId);

   CompletionStage<Optional<DatasetDetails>> findDatasetByName(String projectId, String datasetName);

   CompletionStage<Done> insertOrUpdateDataset(String projectId, DatasetDetails dataset);

}
