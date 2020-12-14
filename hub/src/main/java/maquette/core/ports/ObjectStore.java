package maquette.core.ports;

import akka.Done;
import maquette.core.values.data.binary.BinaryObject;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ObjectStore {

   CompletionStage<Done> save(String key, BinaryObject records);

   CompletionStage<Done> delete(String key);

   CompletionStage<Optional<BinaryObject>> read(String key);

}
