package maquette.adapters.collections;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.ports.ObjectStore;
import maquette.core.values.data.binary.BinaryObject;
import maquette.core.values.data.binary.BinaryObjects;

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
