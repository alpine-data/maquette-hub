package maquette.core.ports;

import akka.Done;
import maquette.core.entities.datasets.model.DatasetProperties;
import maquette.core.entities.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.datasets.model.revisions.Revision;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DatasetsRepository extends HasDataAccessRequests, HasDataAccessTokens, HasDataOwner {

   CompletionStage<List<DatasetProperties>> findAllDatasets();

   CompletionStage<List<DatasetProperties>> findAllDatasets(String projectId);

   CompletionStage<List<Revision>> findAllRevisions(String projectId, String datasetId);

   CompletionStage<List<CommittedRevision>> findAllVersions(String projectId, String datasetId);

   CompletionStage<Optional<DatasetProperties>> findDatasetById(String projectId, String datasetId);

   CompletionStage<Optional<DatasetProperties>> findDatasetByName(String projectId, String datasetName);

   CompletionStage<Optional<Revision>> findRevisionById(String projectId, String datasetId, String revisionId);

   CompletionStage<Optional<CommittedRevision>> findRevisionByVersion(String projectId, String datasetId, String version);

   CompletionStage<Done> insertOrUpdateDataset(String projectId, DatasetProperties dataset);

   CompletionStage<Done> insertOrUpdateRevision(String projectId, String datasetId, Revision revision);

}
