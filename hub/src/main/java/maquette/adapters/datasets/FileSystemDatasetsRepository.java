package maquette.adapters.datasets;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.asset_providers.datasets.DatasetsRepository;
import maquette.asset_providers.datasets.model.CommittedRevision;
import maquette.asset_providers.datasets.model.DatasetVersion;
import maquette.asset_providers.datasets.model.Revision;
import maquette.common.Operators;
import maquette.config.FileSystemRepositoryConfiguration;
import maquette.core.ports.RecordsStore;
import maquette.core.values.UID;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileSystemDatasetsRepository implements DatasetsRepository {

   private static final String PATH = "revisions";

   private static final String FILE_ENDING = ".revision.json";

   private final Path directory;

   private final ObjectMapper om;

   public static FileSystemDatasetsRepository apply(FileSystemRepositoryConfiguration config, ObjectMapper om) {
      var directory = config.getDirectory().resolve("shop");
      Operators.suppressExceptions(() -> Files.createDirectories(directory));

      return new FileSystemDatasetsRepository(directory, om);
   }

   private Path getAssetDirectory(UID asset) {
      var file = directory
         .resolve(asset.getValue())
         .resolve(PATH);

      Operators.suppressExceptions(() -> Files.createDirectories(file));

      return file;
   }

   private Path getRevisionFile(UID asset, UID revision) {
      return getAssetDirectory(asset).resolve(revision.getValue() + FILE_ENDING);
   }

   @Override
   public CompletionStage<List<Revision>> findAllRevisions(UID dataset) {
      var result = Operators
         .suppressExceptions(() -> Files.list(getAssetDirectory(dataset)))
         .filter(file -> file.toString().endsWith(FILE_ENDING))
         .map(file -> Operators.suppressExceptions(() -> om.readValue(file.toFile(), Revision.class)))
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<List<CommittedRevision>> findAllVersions(UID dataset) {
      return findAllRevisions(dataset)
         .thenApply(revisions -> revisions
            .stream()
            .filter(r -> r instanceof CommittedRevision)
            .map(r -> (CommittedRevision) r)
            .collect(Collectors.toList()));
   }

   @Override
   public CompletionStage<Optional<Revision>> findRevisionById(UID dataset, UID revision) {
      var file = getRevisionFile(dataset, revision);

      if (Files.exists(file)) {
         var result = Operators.suppressExceptions(() -> om.readValue(file.toFile(), Revision.class));
         return CompletableFuture.completedFuture(Optional.of(result));
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

   @Override
   public CompletionStage<Optional<CommittedRevision>> findRevisionByVersion(UID dataset, DatasetVersion version) {
      return findAllVersions(dataset)
         .thenApply(versions -> versions
            .stream()
            .filter(v -> v.getVersion().equals(version))
            .findFirst());
   }

   @Override
   public CompletionStage<Done> insertOrUpdateRevision(UID dataset, Revision revision) {
      var file = getRevisionFile(dataset, revision.getId());
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), revision));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public RecordsStore getRecordsStore(UID dataset) {
      var dir = directory.resolve(dataset.getValue()).resolve("records");
      Operators.suppressExceptions(() -> Files.createDirectories(dir));
      return FileSystemRecordsStore.apply(FileSystemRepositoryConfiguration.apply(dir));
   }
}
