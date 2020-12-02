package maquette.adapters.companions;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.model.DatasetVersion;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.data.datasets.model.revisions.Revision;
import maquette.core.values.UID;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DatasetRevisionsFileSystemCompanion {

   private static final String PATH = "revisions";

   private static final String FILE_ENDING = ".revision.json";

   private final Path directory;

   private final ObjectMapper om;

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

   public CompletionStage<List<Revision>> findAllRevisions(UID dataset) {
      var result = Operators
         .suppressExceptions(() -> Files.list(getAssetDirectory(dataset)))
         .filter(file -> file.toString().endsWith(FILE_ENDING))
         .map(file -> Operators.suppressExceptions(() -> om.readValue(file.toFile(), Revision.class)))
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   public CompletionStage<List<CommittedRevision>> findAllVersions(UID dataset) {
      return findAllRevisions(dataset)
         .thenApply(revisions -> revisions
            .stream()
            .filter(r -> r instanceof CommittedRevision)
            .map(r -> (CommittedRevision) r)
            .collect(Collectors.toList()));
   }

   public CompletionStage<Optional<Revision>> findRevisionById(UID dataset, UID revision) {
      var file = getRevisionFile(dataset, revision);

      if (Files.exists(file)) {
         var result = Operators.suppressExceptions(() -> om.readValue(file.toFile(), Revision.class));
         return CompletableFuture.completedFuture(Optional.of(result));
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

   public CompletionStage<Optional<CommittedRevision>> findRevisionByVersion(UID dataset, DatasetVersion version) {
      return findAllVersions(dataset)
         .thenApply(versions -> versions
            .stream()
            .filter(v -> v.getVersion().equals(version))
            .findFirst());
   }

   public CompletionStage<Done> insertOrUpdateRevision(UID dataset, Revision revision) {
      var file = getRevisionFile(dataset, revision.getId());
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), revision));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

}
