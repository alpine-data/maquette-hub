package maquette.core.values.data.binary;

import akka.Done;

import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

public interface BinaryObject {

   FileSize getSize();

   CompletionStage<Done> toFile(Path file);

}
