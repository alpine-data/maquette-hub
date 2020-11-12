package maquette.adapters.datasets;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.datasets.model.DatasetProperties;
import maquette.core.entities.datasets.model.DatasetVersion;
import maquette.core.entities.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.datasets.model.revisions.Revision;
import maquette.core.ports.DatasetsRepository;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessToken;
import maquette.core.values.authorization.UserAuthorization;
import org.checkerframework.checker.nullness.Opt;

import javax.swing.text.html.Option;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileSystemDatasetsRepository implements DatasetsRepository {

   private final FileSystemDatasetsRepositoryConfiguration config;

   private final ObjectMapper om;

   public static FileSystemDatasetsRepository apply(FileSystemDatasetsRepositoryConfiguration config, ObjectMapper om) {
      Operators.suppressExceptions(() -> {
         Files.createDirectories(config.getDirectory());
      });

      return new FileSystemDatasetsRepository(config, om);
   }

   /*
    * Helper functions
    */
   private Path getProjectDirectory(String projectId) {
      return config.getDirectory().resolve(projectId);
   }

   private Path getDatasetsDirectory(String projectId) {
      var path = getProjectDirectory(projectId).resolve("properties");
      Operators.suppressExceptions(() -> Files.createDirectories(path));
      return path;
   }

   private Path getTokensDirectory(String projectId) {
      var path = getProjectDirectory(projectId).resolve("data-access-tokens");
      Operators.suppressExceptions(() -> Files.createDirectories(path));
      return path;
   }

   private Path getRequestsDirectory(String projectId) {
      var path = getProjectDirectory(projectId).resolve("data-access-requests");
      Operators.suppressExceptions(() -> Files.createDirectories(path));
      return path;
   }

   private Path getRevisionsDirectory(String projectId, String datasetId) {
      var path = getProjectDirectory(projectId).resolve(datasetId).resolve("revisions");
      Operators.suppressExceptions(() -> Files.createDirectories(path));
      return path;
   }

   private Path getOwnersDirectory(String projectId) {
      var path = getProjectDirectory(projectId).resolve("owners");
      Operators.suppressExceptions(() -> Files.createDirectories(path));
      return path;
   }

   private Path getDatasetFile(String projectId, String datasetId) {
      return getDatasetsDirectory(projectId).resolve(datasetId + ".json");
   }

   private Path getTokenFile(String parentId, String tokenKey) {
      return getTokensDirectory(parentId).resolve(tokenKey + ".json");
   }

   private Path getRequestFile(String parentId, String requestId) {
      return getRequestsDirectory(parentId).resolve(requestId + ".json");
   }

   private Path getOwnerFile(String projectId, String owner) {
      return getOwnersDirectory(projectId).resolve(owner + ".json");
   }

   private Path getRevisionFile(String projectId, String datasetId, String revisionId) {
      return getRevisionsDirectory(projectId, datasetId).resolve(revisionId + ".json");
   }

   private Optional<DatasetProperties> loadDatasetDetails(String projectId, String datasetId) {
      var file = getDatasetFile(projectId, datasetId);

      if (Files.exists(file) && Files.isRegularFile(file)) {
         return Optional.of(Operators.suppressExceptions(() -> om.readValue(file.toFile(), DatasetProperties.class)));
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

   /*
    * Datasets
    */
   @Override
   public CompletionStage<List<DatasetProperties>> findAllDatasets() {
      var result = Operators.suppressExceptions(() -> Files
         .walk(config.getDirectory())
         .filter(Files::isRegularFile)
         .filter(p -> p.getParent().endsWith("properties"))
         .map(file -> Operators.ignoreExceptionsWithDefault(
            () -> Optional.of(om.readValue(file.toFile(), DatasetProperties.class)),
            Optional.<DatasetProperties>empty()))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .collect(Collectors.toList()));

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<List<DatasetProperties>> findAllDatasets(String projectId) {
      var result = Operators.suppressExceptions(() -> Files
         .list(getDatasetsDirectory(projectId))
         .filter(Files::isRegularFile)
         .map(file -> Operators.ignoreExceptionsWithDefault(
            () -> Optional.of(om.readValue(file.toFile(), DatasetProperties.class)),
            Optional.<DatasetProperties>empty()))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .collect(Collectors.toList()));

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Optional<DatasetProperties>> findDatasetById(String projectId, String datasetId) {
      return CompletableFuture.completedFuture(loadDatasetDetails(projectId, datasetId));
   }

   @Override
   public CompletionStage<Optional<DatasetProperties>> findDatasetByName(String projectId, String datasetName) {
      return findAllDatasets(projectId)
         .thenApply(all -> all
            .stream()
            .filter(d -> d.getName().equals(datasetName))
            .findAny());
   }

   @Override
   public CompletionStage<Done> insertOrUpdateDataset(String projectId, DatasetProperties dataset) {
      var file = getDatasetFile(projectId, dataset.getId());

      Operators.suppressExceptions(() -> {
         try (OutputStream os = Files.newOutputStream(file)) {
            om.writeValue(os, dataset);
         }
      });

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   /*
    * Revisions
    */

   @Override
   public CompletionStage<List<Revision>> findAllRevisions(String projectId, String datasetId) {
      var result = Operators.suppressExceptions(() -> Files
         .list(getRevisionsDirectory(projectId, datasetId))
         .filter(Files::isRegularFile)
         .map(file -> Operators.suppressExceptions(() -> om.readValue(file.toFile(), Revision.class)))
         .collect(Collectors.toList()));

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<List<CommittedRevision>> findAllVersions(String projectId, String datasetId) {
      return findAllRevisions(projectId, datasetId)
         .thenApply(revisions -> revisions
            .stream()
            .filter(r -> r instanceof CommittedRevision)
            .map(r -> (CommittedRevision) r)
            .collect(Collectors.toList()));
   }

   @Override
   public CompletionStage<Optional<Revision>> findRevisionById(String projectId, String datasetId, String revisionId) {
      var file = getRevisionFile(projectId, datasetId, revisionId);

      if (Files.exists(file) && Files.isRegularFile(file)) {
         var revision = Operators.suppressExceptions(() -> om.readValue(file.toFile(), Revision.class));
         return CompletableFuture.completedFuture(Optional.of(revision));
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

   @Override
   public CompletionStage<Optional<CommittedRevision>> findRevisionByVersion(String projectId, String datasetId, DatasetVersion version) {
      return findAllVersions(projectId, datasetId)
         .thenApply(versions -> versions
            .stream()
            .filter(v -> v.getVersion().equals(version))
            .findFirst());
   }

   @Override
   public CompletionStage<Done> insertOrUpdateRevision(String projectId, String datasetId, Revision revision) {
      var file = getRevisionFile(projectId, datasetId, revision.getId());

      Operators.suppressExceptions(() -> {
         try (OutputStream os = Files.newOutputStream(file)) {
            om.writeValue(os, revision);
         }
      });

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   /*
    * Data Access Requests
    */

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
         .list(getRequestsDirectory(parentId))
         .filter(Files::isRegularFile)
         .map(file -> Operators.suppressExceptions(() -> om.readValue(file.toFile(), DataAccessRequest.class)))
         .collect(Collectors.toList()));

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> removeDataAccessRequest(String parentId, String id) {
      var file = getRequestFile(parentId, id);
      Operators.ignoreExceptions(() -> Files.deleteIfExists(file));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   /*
    * Data Access Tokens
    */
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
         .list(getTokensDirectory(parentId))
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

   /*
    * Owners
    */
   @Override
   public CompletionStage<List<UserAuthorization>> findAllOwners(String parentId) {
      var owners = Operators.suppressExceptions(() -> Files
         .list(getOwnersDirectory(parentId))
         .filter(Files::isRegularFile)
         .map(p -> Operators.suppressExceptions(() -> om.readValue(p.toFile(), UserAuthorization.class)))
         .collect(Collectors.toList()));

      return CompletableFuture.completedFuture(owners);
   }

   @Override
   public CompletionStage<Done> insertOwner(String parentId, UserAuthorization owner) {
      var file = getOwnerFile(parentId, owner.getUserId());
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), owner));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Done> removeOwner(String parentId, String userId) {
      var file = getOwnerFile(parentId, userId);
      Operators.suppressExceptions(() -> Files.deleteIfExists(file));
      return CompletableFuture.completedFuture(Done.getInstance());
   }
}
