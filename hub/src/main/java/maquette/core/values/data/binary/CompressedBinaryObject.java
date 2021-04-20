package maquette.core.values.data.binary;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@AllArgsConstructor(staticName = "apply")
public final class CompressedBinaryObject implements BinaryObject {

   private final BinaryObject object;

   static CompressedBinaryObject fromDirectory(Path directory) {
      return Operators.suppressExceptions(() -> {
         var baos = new ByteArrayOutputStream();

         try (var zos = new ZipOutputStream(baos)) {

            Files.walk(directory)
               .filter(Files::isRegularFile)
               .forEach(path -> Operators.suppressExceptions(() -> {
                  var name = directory.relativize(path).toString();
                  var entry = new ZipEntry(name);

                  try (var fis = Files.newInputStream(path)) {
                     zos.putNextEntry(entry);
                     byte[] bytes = new byte[1024];
                     while (fis.read(bytes) >= 0) {
                        zos.write(bytes);
                     }
                  }
               }));
         }

         var zipped = ByteArrayBinaryObject.apply(baos.toByteArray());
         return CompressedBinaryObject.apply(zipped);
      });
   }

   @Override
   public FileSize getSize() {
      return object.getSize();
   }

   @Override
   public CompletionStage<Done> toFile(Path file) {
      return Operators.suppressExceptions(() -> {
         var result = CompletableFuture.completedFuture(Done.getInstance());

         try (var zis = new ZipInputStream(object.toInputStream())) {
            var zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
               if (!zipEntry.isDirectory()) {
                  var bin = BinaryObjects.fromInputStream(zis);
                  var name = zipEntry.getName();

                  var target = file.resolve(name);
                  Operators.suppressExceptions(() -> Files.createDirectories(target.getParent()));
                  bin.toFile(target);
               }

               zipEntry = zis.getNextEntry();
            }
         }

         return result;
      });
   }

   @Override
   public InputStream toInputStream() {
      return object.toInputStream();
   }

}
