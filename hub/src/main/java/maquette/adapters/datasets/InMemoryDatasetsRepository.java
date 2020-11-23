package maquette.adapters.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasets.model.DatasetVersion;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.data.datasets.model.revisions.Revision;
import maquette.core.ports.DatasetsRepository;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessToken;
import maquette.core.values.authorization.UserAuthorization;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.NotImplementedException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryDatasetsRepository implements DatasetsRepository {

   private final List<StoredDataset> datasets;

   private final List<DataAccessRequest> requests;

   private final List<StoredDataAccessToken> tokens;

   public static InMemoryDatasetsRepository apply() {
      return apply(Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList());
   }

   /*
    * Datasets
    */
   @Override
   public CompletionStage<List<DatasetProperties>> findAllDatasets() {
      var result = datasets
         .stream()
         .map(StoredDataset::getDetails)
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<List<DatasetProperties>> findAllDatasets(String projectId) {
      var result = datasets
         .stream()
         .filter(details -> details.parentId.equals(projectId))
         .map(StoredDataset::getDetails)
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Optional<DatasetProperties>> findDatasetById(String projectId, String datasetId) {
      var result = datasets
         .stream()
         .filter(details -> details.parentId.equals(projectId) && details.getDetails().getId().equals(datasetId))
         .findFirst()
         .map(StoredDataset::getDetails);

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Optional<DatasetProperties>> findDatasetByName(String projectId, String datasetName) {
      var result = datasets
         .stream()
         .filter(details -> details.parentId.equals(projectId) && details.getDetails().getName().equals(datasetName))
         .findFirst()
         .map(StoredDataset::getDetails);

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateDataset(String projectId, DatasetProperties dataset) {
      datasets
         .stream()
         .filter(d -> d.parentId.equals(projectId) && d.getDetails().getId().equals(dataset.getId()))
         .forEach(datasets::remove);

      datasets.add(StoredDataset.apply(projectId, dataset));

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   /*
    * Revisions
    */
   @Override
   public CompletionStage<List<Revision>> findAllRevisions(String projectId, String datasetId) {
      throw new NotImplementedException();
   }

   @Override
   public CompletionStage<List<CommittedRevision>> findAllVersions(String projectId, String datasetId) {
      throw new NotImplementedException();
   }

   @Override
   public CompletionStage<Optional<Revision>> findRevisionById(String projectId, String datasetId, String revisionId) {
      throw new NotImplementedException();
   }

   @Override
   public CompletionStage<Optional<CommittedRevision>> findRevisionByVersion(String projectId, String datasetId, DatasetVersion version) {
      throw new NotImplementedException();
   }

   @Override
   public CompletionStage<Done> insertOrUpdateRevision(String projectId, String datasetId, Revision revision) {
      throw new NotImplementedException();
   }

   /*
    * Data Access Requests
    */

   @Override
   public CompletionStage<Optional<DataAccessRequest>> findDataAccessRequestById(String targetProjectId, String targetId, String id) {
      var result = requests
         .stream()
         .filter(r -> r.getTargetProjectId().equals(targetProjectId) && r.getTargetId().equals(targetId) && r.getId().equals(id))
         .findFirst();

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateDataAccessRequest(DataAccessRequest request) {
      requests
         .stream()
         .filter(r -> r.getTargetProjectId().equals(request.getTargetProjectId()) &&
            r.getTargetId().equals(request.getTargetId()) &&
            r.getId().equals(request.getId()))
         .forEach(requests::remove);

      requests.add(request);

      return CompletableFuture.completedStage(Done.getInstance());
   }

   @Override
   public CompletionStage<List<DataAccessRequest>> findDataAccessRequestsByParent(String targetProjectId, String targetId) {
      var result = requests
         .stream()
         .filter(r -> r.getTargetProjectId().equals(targetProjectId) && r.getTargetId().equals(targetId))
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<List<DataAccessRequest>> findDataAccessRequestsByOrigin(String originId) {
      var result = requests
         .stream()
         .filter(r -> r.getOriginProjectId().equals(originId))
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> removeDataAccessRequest(String targetProjectId, String targetId, String id) {
      requests
         .stream()
         .filter(r -> r.getTargetProjectId().equals(targetProjectId) &&
            r.getTargetId().equals(targetId) &&
            r.getId().equals(id))
         .forEach(requests::remove);

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   /*
    * Data Access Tokens
    */

   @Override
   public CompletionStage<Done> insertDataAccessToken(String parentId, DataAccessToken token) {
      tokens
         .stream()
         .filter(t -> t.getParentId().equals(parentId) && t.getDataAccessToken().getKey().equals(token.getKey()))
         .forEach(tokens::remove);

      tokens.add(StoredDataAccessToken.apply(parentId, token));

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Optional<DataAccessToken>> findDataAccessTokenByKey(String parentId, String key) {
      var result = tokens
         .stream()
         .filter(t -> t.getParentId().equals(parentId) && t.getDataAccessToken().getKey().equals(key))
         .map(StoredDataAccessToken::getDataAccessToken)
         .findFirst();

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<List<DataAccessToken>> findDataAccessTokensByParent(String parentId) {
      var result = tokens
         .stream()
         .filter(t -> t.getParentId().equals(parentId))
         .map(StoredDataAccessToken::getDataAccessToken)
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> removeDataAccessToken(String parentId, String key) {
      tokens
         .stream()
         .filter(t -> t.getParentId().equals(parentId) && t.getDataAccessToken().getKey().equals(key))
         .forEach(tokens::remove);

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   /*
    * Owners
    */

   @Override
   public CompletionStage<List<UserAuthorization>> findAllOwners(String parentId) {
      throw new NotImplementedException();
   }

   @Override
   public CompletionStage<Done> insertOwner(String parentId, UserAuthorization owner) {
      throw new NotImplementedException();
   }

   @Override
   public CompletionStage<Done> removeOwner(String parentId, String userId) {
      throw new NotImplementedException();
   }

   /*
    * Helpers
    */

   @Value
   @AllArgsConstructor(staticName = "apply")
   private static class StoredDataset {

      String parentId;

      DatasetProperties details;

   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   private static class StoredDataAccessToken {

      String parentId;

      DataAccessToken dataAccessToken;

   }

}
