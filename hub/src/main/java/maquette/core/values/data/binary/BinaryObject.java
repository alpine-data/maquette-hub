package maquette.core.values.data.binary;

import java.nio.file.Path;

public interface BinaryObject {

   FileSize getSize();

   void toFile(Path file);

}
