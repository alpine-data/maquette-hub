package maquette.core.values.data.binary;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import net.lingala.zip4j.ZipFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class CompressedBinaryObject implements BinaryObject {

   private final BinaryObject object;

   static CompressedBinaryObject fromFile(Path file) {
      return Operators.suppressExceptions(() -> {
         var zipFile = Files.createTempFile("mq", ".zip");
         Files.delete(zipFile);

         var zip = new ZipFile(zipFile.toFile());
         zip.addFile(file.toFile());

         var object = ByteArrayBinaryObject.fromFile(zipFile);
         Files.delete(zipFile);

         return CompressedBinaryObject.apply(object);
      });
   }

   static CompressedBinaryObject fromDirectory(Path directory) {
      return Operators.suppressExceptions(() -> {
         var zipFile = Files.createTempFile("mq", ".zip");
         Files.delete(zipFile);

         var zip = new ZipFile(zipFile.toFile());
         Files
            .list(directory)
            .forEach(path -> Operators.suppressExceptions(() -> {
               if (Files.isDirectory(path)) {
                  zip.addFolder(path.toFile());
               } else {
                  zip.addFile(path.toFile());
               }
            }));

         var object = ByteArrayBinaryObject.fromFile(zipFile);
         Files.delete(zipFile);

         return CompressedBinaryObject.apply(object);
      });
   }

   @Override
   public FileSize getSize() {
      return object.getSize();
   }

   @Override
   public CompletionStage<Done> toFile(Path file) {
      return Operators.suppressExceptions(() -> {
         var zipFile = Files.createTempFile("mq", ".zip");
         Files.delete(zipFile);

         return object
            .toFile(zipFile)
            .thenApply(done -> {
               var zip = new ZipFile(zipFile.toFile());

               Operators.suppressExceptions(() -> {
                  zip.extractAll(file.toFile().getAbsolutePath());
                  Files.deleteIfExists(zipFile);
               });

               return Done.getInstance();
            });
      });
   }

   @Override
   public InputStream toInputStream() {
      return object.toInputStream();
   }

}
