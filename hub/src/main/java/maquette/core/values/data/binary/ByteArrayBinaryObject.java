package maquette.core.values.data.binary;

import lombok.AllArgsConstructor;
import maquette.common.Operators;

import java.nio.file.Files;
import java.nio.file.Path;

@AllArgsConstructor(staticName = "apply")
class ByteArrayBinaryObject implements BinaryObject {

   private final byte[] bytes;

   @Override
   public FileSize getSize() {
      return FileSize.apply(bytes.length, FileSize.Unit.BYTES);
   }

   @Override
   public void toFile(Path file) {
      Operators.suppressExceptions(() -> {
         try (var os = Files.newOutputStream(file)) {
            os.write(bytes);
         }
      });
   }

}
