package maquette.datashop.providers.collections.ports;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.binary.BinaryObject;
import maquette.core.values.binary.BinaryObjects;
import maquette.datashop.ports.ObjectStore;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class FileSystemObjectsStore implements ObjectStore {

   private final Path directory;

   @Override
   public CompletionStage<Done> saveObject(String key, BinaryObject binary) {
      var file = directory.resolve(key).toAbsolutePath();
      Operators.suppressExceptions(() -> Files.createDirectories(file.getParent()));

      binary.toFile(directory.resolve(key));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Done> deleteObject(String key) {
      Operators.suppressExceptions(() -> Files.deleteIfExists(directory.resolve(key)));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Optional<BinaryObject>> readObject(String key) {
      var file = directory.resolve(key);

      if (Files.exists(file)) {
         var result = Optional.of(BinaryObjects.fromFile(file));
         return CompletableFuture.completedFuture(result);
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

}
