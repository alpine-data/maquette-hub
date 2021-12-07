package maquette.core.values.binary;

import akka.Done;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PROTECTED)
final class ByteArrayBinaryObject implements BinaryObject {

   private final byte[] bytes;

   public static ByteArrayBinaryObject fromFile(Path file) {
      return apply(Operators.suppressExceptions(() -> Files.readAllBytes(file)));
   }

   @Override
   public FileSize getSize() {
      return FileSize.apply(bytes.length, FileSize.Unit.BYTES);
   }

   @Override
   public CompletableFuture<Done> toFile(Path file) {
      Operators.suppressExceptions(() -> {
         Files.deleteIfExists(file);

         try (var os = Files.newOutputStream(file)) {
            os.write(bytes);
         }
      });


      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public InputStream toInputStream() {
      return new ByteArrayInputStream(bytes);
   }

}
