package maquette.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeleteOnCloseFileInputStream extends InputStream {

   private final Path file;

   private final InputStream delegate;

   public static DeleteOnCloseFileInputStream apply(Path file) {
      return new DeleteOnCloseFileInputStream(file, Operators.suppressExceptions(() -> Files.newInputStream(file)));
   }

   @Override
   public int read() throws IOException {
      return delegate.read();
   }

   @Override
   public void close() throws IOException {
      delegate.close();
      Files.deleteIfExists(file);
   }

}
