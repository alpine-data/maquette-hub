package maquette.core.values.binary;

import akka.Done;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface BinaryObject {

   /**
    * Returns the file size of the binary object.
    *
    * @return The size.
    */
   FileSize getSize();

   /**
    * Writes the object to a file.
    *
    * @param file The path to which the data should be written.
    * @return Done
    */
   CompletionStage<Done> toFile(Path file);

   /**
    * Creates an input stream for the binary object.
    *
    * @return A fresh input stream.
    */
   InputStream toInputStream();

   /**
    * Signals the binary object that temporary files can be discarded. After calling discard the object
    * should not be used anymore!
    *
    * @return Done
    */
   default CompletionStage<Done> discard() {
      return CompletableFuture.completedFuture(Done.getInstance());
   }

}
