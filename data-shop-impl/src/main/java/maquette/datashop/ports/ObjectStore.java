package maquette.datashop.ports;

import akka.Done;
import maquette.core.values.binary.BinaryObject;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ObjectStore {

   /**
    * Saves a binary object to the object store.
    *
    * @param key The binary object key.
    * @param binary The binary object.
    * @return Done.
    */
   CompletionStage<Done> saveObject(String key, BinaryObject binary);

   /**
    * Deletes a binary object from the object store.
    *
    * @param key The binary object key.
    * @return Done.
    */
   CompletionStage<Done> deleteObject(String key);

   /**
    * Reads a binary object from the object store.
    *
    * @param key The binary object key.
    * @return The binary object read from the file system.
    */
   CompletionStage<Optional<BinaryObject>> readObject(String key);

}
