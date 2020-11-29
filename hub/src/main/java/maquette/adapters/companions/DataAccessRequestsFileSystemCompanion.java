package maquette.adapters.companions;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.ports.HasDataAccessRequests;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequestProperties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DataAccessRequestsFileSystemCompanion implements HasDataAccessRequests {

   private static final String PATH = "requests";

   private static final String FILE_ENDING = ".request.json";

   private final Path directory;

   private final ObjectMapper om;

   private Path getAssetDirectory(UID asset) {
      var file = directory
         .resolve(asset.getValue())
         .resolve(PATH);

      Operators.suppressExceptions(() -> Files.createDirectories(file));

      return file;
   }

   private Path getRequestFile(UID asset, UID request) {
      return getAssetDirectory(asset).resolve(request.getValue() + FILE_ENDING);
   }

   @Override
   public CompletionStage<Optional<DataAccessRequestProperties>> findDataAccessRequestById(UID asset, UID request) {
      var file = getRequestFile(asset, request);

      if (Files.exists(file)) {
         var r = Operators.suppressExceptions(() -> om.readValue(file.toFile(), DataAccessRequestProperties.class));
         return CompletableFuture.completedFuture(Optional.of(r));
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

   @Override
   public CompletionStage<Done> insertOrUpdateDataAccessRequest(DataAccessRequestProperties request) {
      var file = getRequestFile(request.getAsset(), request.getId());
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), request));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByProject(UID project) {
      var result = Operators
         .suppressExceptions(() -> Files.list(directory))
         .filter(Files::isDirectory)
         .map(directory -> directory.resolve(PATH))
         .filter(Files::isDirectory)
         .flatMap(directory -> Operators.suppressExceptions(() -> Files.list(directory)))
         .filter(file -> file.toString().endsWith(FILE_ENDING))
         .map(file -> Operators.suppressExceptions(() -> om.readValue(file.toFile(), DataAccessRequestProperties.class)))
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByAsset(UID asset) {
      var result = Operators
         .suppressExceptions(() -> Files.list(getAssetDirectory(asset)))
         .filter(file -> file.toString().endsWith(FILE_ENDING))
         .map(file -> Operators.suppressExceptions(() -> om.readValue(file.toFile(), DataAccessRequestProperties.class)))
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> removeDataAccessRequest(UID asset, UID id) {
      var file = getRequestFile(asset, id);
      Operators.suppressExceptions(() -> Files.deleteIfExists(file));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

}
