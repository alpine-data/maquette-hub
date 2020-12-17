package maquette.core.values.data.binary;

import akka.Done;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PROTECTED)
public final class FileBinaryObject implements BinaryObject {

   private final Path file;

   @Override
   public FileSize getSize() {
      return Operators.suppressExceptions(() -> FileSize.apply(Files.size(file), FileSize.Unit.BYTES));
   }

   @Override
   public CompletionStage<Done> toFile(Path file) {
      Operators.suppressExceptions(() -> Files.copy(this.file, file));
      return CompletableFuture.completedFuture(Done.getInstance());
   }
}
