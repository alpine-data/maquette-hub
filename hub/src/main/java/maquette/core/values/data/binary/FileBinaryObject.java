package maquette.core.values.data.binary;

import akka.Done;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public final class FileBinaryObject implements BinaryObject {

   private final Path file;

   private final boolean temporary;

   private boolean discarded;

   static FileBinaryObject apply(Path file, boolean temporary) {
      return apply(file, temporary, false);
   }

   @Override
   public FileSize getSize() {
      if (discarded) {
         throw new IllegalStateException("The object has been discarded already!");
      }

      return Operators.suppressExceptions(() -> FileSize.apply(Files.size(file), FileSize.Unit.BYTES));
   }

   @Override
   public CompletionStage<Done> toFile(Path file) {
      if (discarded) {
         throw new IllegalStateException("The object has been discarded already!");
      }

      Operators.suppressExceptions(() -> {
         Files.deleteIfExists(file);
         Files.copy(this.file, file);
      });
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public InputStream toInputStream() {
      return Operators.suppressExceptions(() -> new FileInputStream(file.toFile()));
   }

   @Override
   public CompletionStage<Done> discard() {
      if (temporary) {
         Operators.ignoreExceptions(() -> Files.deleteIfExists(file));
      }

      discarded = true;
      return CompletableFuture.completedFuture(Done.getInstance());
   }
}
