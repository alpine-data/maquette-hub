package maquette.adapters.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.datasets.model.DatasetDetails;
import maquette.core.ports.DatasetsRepository;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessToken;
import maquette.core.values.authorization.UserAuthorization;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryDatasetsRepository implements DatasetsRepository {

   private final List<StoredDataset> datasets;

   private final List<StoredDataAccessRequest> requests;

   private final List<StoredDataAccessToken> tokens;

   public static InMemoryDatasetsRepository apply() {
      return apply(Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList());
   }

   @Override
   public CompletionStage<List<DatasetDetails>> findAllDatasets() {
      var result = datasets
         .stream()
         .map(StoredDataset::getDetails)
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<List<DatasetDetails>> findAllDatasets(String projectId) {
      var result = datasets
         .stream()
         .filter(details -> details.parentId.equals(projectId))
         .map(StoredDataset::getDetails)
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Optional<DatasetDetails>> findDatasetById(String projectId, String datasetId) {
      var result = datasets
         .stream()
         .filter(details -> details.parentId.equals(projectId) && details.getDetails().getId().equals(datasetId))
         .findFirst()
         .map(StoredDataset::getDetails);

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Optional<DatasetDetails>> findDatasetByName(String projectId, String datasetName) {
      var result = datasets
         .stream()
         .filter(details -> details.parentId.equals(projectId) && details.getDetails().getName().equals(datasetName))
         .findFirst()
         .map(StoredDataset::getDetails);

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateDataset(String projectId, DatasetDetails dataset) {
      datasets
         .stream()
         .filter(d -> d.parentId.equals(projectId) && d.getDetails().getId().equals(dataset.getId()))
         .forEach(datasets::remove);

      datasets.add(StoredDataset.apply(projectId, dataset));

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Optional<DataAccessRequest>> findDataAccessRequestById(String parentId, String id) {
      var result = requests
         .stream()
         .filter(r -> r.getParentId().equals(parentId) && r.getDataAccessRequest().getId().equals(id))
         .map(StoredDataAccessRequest::getDataAccessRequest)
         .findFirst();

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateDataAccessRequest(String parentId, DataAccessRequest request) {
      requests
         .stream()
         .filter(r -> r.getParentId().equals(parentId) && r.getDataAccessRequest().getId().equals(request.getId()))
         .forEach(requests::remove);

      requests.add(StoredDataAccessRequest.apply(parentId, request));

      return CompletableFuture.completedStage(Done.getInstance());
   }

   @Override
   public CompletionStage<List<DataAccessRequest>> findDataAccessRequestsByParent(String parentId) {
      var result = requests
         .stream()
         .filter(r -> r.getParentId().equals(parentId))
         .map(StoredDataAccessRequest::getDataAccessRequest)
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> removeDataAccessRequest(String parentId, String id) {
      requests
         .stream()
         .filter(r -> r.getParentId().equals(parentId) && r.getDataAccessRequest().getId().equals(id))
         .forEach(requests::remove);

      return CompletableFuture.completedFuture(Done.getInstance());
   }

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

   @Override
   public CompletionStage<List<UserAuthorization>> findAllOwners(String parentId) {
      return null;
   }

   @Override
   public CompletionStage<Done> insertOwner(String parentId, UserAuthorization owner) {
      return null;
   }

   @Override
   public CompletionStage<Done> removeOwner(String parentId, String userId) {
      return null;
   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   private static class StoredDataset {

      String parentId;

      DatasetDetails details;

   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   private static class StoredDataAccessRequest {

      String parentId;

      DataAccessRequest dataAccessRequest;

   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   private static class StoredDataAccessToken {

      String parentId;

      DataAccessToken dataAccessToken;

   }

}
