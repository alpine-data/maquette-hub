package maquette.adapters.companions;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.ports.common.DataAssetRepository;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetProperties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileSystemDataAssetRepository<T extends DataAssetProperties<T>> implements DataAssetRepository<T> {

   private static final String PROPERTIES_FILE = "asset.json";

   private final Class<T> type;

   private final Path directory;

   private final ObjectMapper om;

   public static <T extends DataAssetProperties<T>> FileSystemDataAssetRepository<T> apply(Class<T> type, Path directory, ObjectMapper om) {
      Operators.suppressExceptions(() -> Files.createDirectories(directory));
      return new FileSystemDataAssetRepository<>(type, directory, om);
   }

   private Path getAssetDirectory(UID dataset) {
      var dir = directory.resolve(dataset.getValue());
      Operators.suppressExceptions(() -> Files.createDirectories(dir));
      return dir;
   }

   private Path getAssetFile(UID asset) {
      return getAssetDirectory(asset).resolve(PROPERTIES_FILE);
   }

   @Override
   public CompletionStage<List<T>> findAllAssets() {
      var result = Operators
         .suppressExceptions(() -> Files.list(directory))
         .filter(Files::isDirectory)
         .map(directory -> directory.resolve(PROPERTIES_FILE))
         .filter(Files::exists)
         .map(file -> Operators.suppressExceptions(() -> om.readValue(file.toFile(), type)))
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Optional<T>> findAssetById(UID asset) {
      var file = getAssetFile(asset);

      if (Files.exists(file)) {
         var result = Operators.suppressExceptions(() -> om.readValue(file.toFile(), type));
         return CompletableFuture.completedFuture(Optional.of(result));
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

   @Override
   public CompletionStage<Optional<T>> findAssetByName(String name) {
      return findAllAssets()
         .thenApply(datasets -> datasets
            .stream()
            .filter(d -> d.getName().equals(name))
            .findFirst());
   }

   @Override
   public CompletionStage<Done> insertOrUpdateAsset(T asset) {
      var file = getAssetFile(asset.getId());
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), asset));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

}
