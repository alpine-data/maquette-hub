package maquette.core.ports;

import akka.Done;
import maquette.core.values.data.binary.BinaryObject;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ObjectStore {

   CompletionStage<Done> saveObject(String key, BinaryObject binary);

   CompletionStage<Done> deleteObject(String key);

   CompletionStage<Optional<BinaryObject>> readObject(String key);

}
