package maquette.adapters.datasets;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.datasets.model.DatasetDetails;
import maquette.core.ports.DatasetsRepository;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessToken;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class FileSystemDatasetsRepository implements DatasetsRepository {

   private final FileSystemDatasetsRepositoryConfiguration config;

   private final ObjectMapper om;

   private Path getDatasetsDirectory() {
      return config.getDirectory().resolve("details");
   }

   private Path getTokensDirectory() {
      return config.getDirectory().resolve("data-access-tokens");
   }

   private Path getRequestsDirectory() {
      return config.getDirectory().resolve("data-access-requests");
   }

   private Path getDatasetFile(String projectId, String datasetId) {
      return getDatasetsDirectory().resolve(projectId).resolve(datasetId + ".json");
   }

   private Path getTokenFile(String parentId, String tokenKey) {
      return getTokensDirectory().resolve(parentId).resolve(tokenKey + ".json");
   }

   private Path getRequestFile(String parentId, String requestId) {
      return getRequestsDirectory().resolve(parentId).resolve(requestId + ".json");
   }

   private Optional<DatasetDetails> loadDatasetDetails(String projectId, String datasetId) {
      var file = getDatasetFile(projectId, datasetId);

      if (Files.exists(file) && Files.isRegularFile(file)) {
         return Optional.of(Operators.suppressExceptions(() -> om.readValue(file.toFile(), DatasetDetails.class)));
      } else {
         return Optional.empty();
      }
   }

   private Optional<DataAccessRequest> loadDataAccessRequest(String parentId, String id) {
      var file = getRequestFile(parentId, id);

      if (Files.exists(file) && Files.isRegularFile(file)) {
         return Optional.of(Operators.suppressExceptions(() -> om.readValue(file.toFile(), DataAccessRequest.class)));
      } else {
         return Optional.empty();
      }
   }

   private Optional<DataAccessToken> loadDataAccessToken(String parentId, String key) {
      var file = getTokenFile(parentId, key);

      if (Files.exists(file) && Files.isRegularFile(file)) {
         return Optional.of(Operators.suppressExceptions(() -> om.readValue(file.toFile(), DataAccessToken.class)));
      } else {
         return Optional.empty();
      }
   }

   @Override
   public CompletionStage<List<DatasetDetails>> findAllDatasets() {
      var result = Operators.suppressExceptions(() -> Files
         .walk(getDatasetsDirectory())
         .filter(Files::isRegularFile)
         .map(file -> Operators.ignoreExceptionsWithDefault(
            () -> Optional.of(om.readValue(file.toFile(), DatasetDetails.class)),
            Optional.<DatasetDetails>empty()))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .collect(Collectors.toList()));

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<List<DatasetDetails>> findAllDatasets(String projectId) {
      var result = Operators.suppressExceptions(() -> Files
         .list(getDatasetsDirectory().resolve(projectId))
         .filter(Files::isRegularFile)
         .map(file -> Operators.ignoreExceptionsWithDefault(
            () -> Optional.of(om.readValue(file.toFile(), DatasetDetails.class)),
            Optional.<DatasetDetails>empty()))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .collect(Collectors.toList()));

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Optional<DatasetDetails>> findDatasetById(String projectId, String datasetId) {
      return CompletableFuture.completedFuture(loadDatasetDetails(projectId, datasetId));
   }

   @Override
   public CompletionStage<Optional<DatasetDetails>> findDatasetByName(String projectId, String datasetName) {
      return findAllDatasets(projectId)
         .thenApply(all -> all
            .stream()
            .filter(d -> d.getName().equals(datasetName))
            .findAny());
   }

   @Override
   public CompletionStage<Done> insertOrUpdateDataset(String projectId, DatasetDetails dataset) {
      var file = getDatasetFile(projectId, dataset.getId());

      Operators.suppressExceptions(() -> {
         try (OutputStream os = Files.newOutputStream(file)) {
            om.writeValue(os, dataset);
         }
      });

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Optional<DataAccessRequest>> findDataAccessRequestById(String parentId, String id) {
      return CompletableFuture.completedFuture(loadDataAccessRequest(parentId, id));
   }

   @Override
   public CompletionStage<Done> insertOrUpdateDataAccessRequest(String parentId, DataAccessRequest request) {
      var file = getRequestFile(parentId, request.getId());

      Operators.suppressExceptions(() -> {
         try (OutputStream os = Files.newOutputStream(file)) {
            om.writeValue(os, request);
         }
      });

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<List<DataAccessRequest>> findDataAccessRequestsByParent(String parentId) {
      var result = Operators.suppressExceptions(() -> Files
         .list(getRequestsDirectory().resolve(parentId))
         .filter(Files::isRegularFile)
         .map(file -> Operators.ignoreExceptionsWithDefault(
            () -> Optional.of(om.readValue(file.toFile(), DataAccessRequest.class)),
            Optional.<DataAccessRequest>empty()))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .collect(Collectors.toList()));

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> removeDataAccessRequest(String parentId, String id) {
      var file = getRequestFile(parentId, id);
      Operators.ignoreExceptions(() -> Files.deleteIfExists(file));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Done> insertDataAccessToken(String parentId, DataAccessToken token) {
      var file = getTokenFile(parentId, token.getKey());

      Operators.suppressExceptions(() -> {
         try (OutputStream os = Files.newOutputStream(file)) {
            om.writeValue(os, token);
         }
      });

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Optional<DataAccessToken>> findDataAccessTokenByKey(String parentId, String key) {
      return CompletableFuture.completedFuture(loadDataAccessToken(parentId, key));
   }

   @Override
   public CompletionStage<List<DataAccessToken>> findDataAccessTokensByParent(String parentId) {
      var result = Operators.suppressExceptions(() -> Files
         .list(getTokensDirectory().resolve(parentId))
         .filter(Files::isRegularFile)
         .map(file -> Operators.ignoreExceptionsWithDefault(
            () -> Optional.of(om.readValue(file.toFile(), DataAccessToken.class)),
            Optional.<DataAccessToken>empty()))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .collect(Collectors.toList()));

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> removeDataAccessToken(String parentId, String key) {
      var file = getTokenFile(parentId, key);
      Operators.ignoreExceptions(() -> Files.deleteIfExists(file));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

}
