package maquette.core.values.data.binary;

import java.nio.file.Path;

public final class BinaryObjects {

   public static BinaryObject fromBytes(byte[] bytes) {
      return ByteArrayBinaryObject.apply(bytes);
   }

   public static BinaryObject fromFile(Path file) {
      return FileBinaryObject.apply(file);
   }

}
