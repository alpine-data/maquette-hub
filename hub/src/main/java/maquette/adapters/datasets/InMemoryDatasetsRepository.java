package maquette.adapters.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.datasets.model.DatasetDetails;
import maquette.core.ports.DatasetsRepository;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessToken;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryDatasetsRepository implements DatasetsRepository {

   @Override
   public CompletionStage<List<DatasetDetails>> findAllDatasets() {
      return null;
   }

   @Override
   public CompletionStage<List<DatasetDetails>> findAllDatasets(String projectId) {
      return null;
   }

   @Override
   public CompletionStage<Optional<DatasetDetails>> findDatasetById(String projectId, String datasetId) {
      return null;
   }

   @Override
   public CompletionStage<Optional<DatasetDetails>> findDatasetByName(String projectId, String datasetName) {
      return null;
   }

   @Override
   public CompletionStage<Done> insertOrUpdateDataset(String projectId, DatasetDetails dataset) {
      return null;
   }

   @Override
   public CompletionStage<Optional<DataAccessRequest>> findDataAccessRequestById(String parentId, String id) {
      return null;
   }

   @Override
   public CompletionStage<Done> insertOrUpdateDataAccessRequest(String parentId, DataAccessRequest request) {
      return null;
   }

   @Override
   public CompletionStage<List<DataAccessRequest>> findDataAccessRequestsByParent(String parentId) {
      return null;
   }

   @Override
   public CompletionStage<Done> removeDataAccessRequest(String parentId, String id) {
      return null;
   }

   @Override
   public CompletionStage<Done> insertDataAccessToken(String parentId, DataAccessToken token) {
      return null;
   }

   @Override
   public CompletionStage<Optional<DataAccessToken>> findDataAccessTokenByKey(String parentId, String key) {
      return null;
   }

   @Override
   public CompletionStage<List<DataAccessToken>> findAllDataAccessTokens(String parentId) {
      return null;
   }

   @Override
   public CompletionStage<Done> removeDataAccessToken(String parentId, String key) {
      return null;
   }

}
