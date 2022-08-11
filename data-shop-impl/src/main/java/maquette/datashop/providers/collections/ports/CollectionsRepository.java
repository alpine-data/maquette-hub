package maquette.datashop.providers.collections.ports;

import akka.Done;
import maquette.core.values.UID;
import maquette.core.values.binary.BinaryObject;
import maquette.datashop.providers.collections.model.CollectionTag;
import maquette.datashop.providers.collections.model.FileEntry;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface CollectionsRepository {

   /**
    * Returns all tags of a collection.
    *
    * @param collection The UID of the collection.
    * @return All tags of the collection.
    */
   CompletionStage<List<CollectionTag>> findAllTags(UID collection);

   /**
    * Finds and returns the tag with the given name.
    *
    * @param collection The UID of the collection.
    * @return The tag with the given name.
    */
   CompletionStage<Optional<CollectionTag>> findTagByName(UID collection, String name);

   /**
    * Inserts a new tag to the collection. If the tag already exists, the existing tag is updated.
    *
    * @param collection The UID of the collection.
    * @param tag The tag to be added to the collection.
    * @return Done.
    */
   CompletionStage<Done> insertOrUpdateTag(UID collection, CollectionTag tag);

   /**
    * Persist multiple file objects contained in a directory.
    *
    * @param collection The UID of the collection.
    * @param files The directory to be persisted.
    * @return Done.
    */
   CompletionStage<Done> saveFiles(UID collection, FileEntry.Directory files);

   /**
    * Unpersists all files of a collection if the persisted files exist.
    *
    * @param collection The UID of the collection.
    * @return The unpersisted directory object.
    */
   CompletionStage<FileEntry.Directory> getFiles(UID collection);

   /**
    * Saves a BinaryObject to the object store.
    *
    * @param collection The UID of the collection.
    * @param key The binary object key.
    * @param binary The binary object.
    * @return Done.
    */
   CompletionStage<Done> saveObject(UID collection, String key, BinaryObject binary);

   /**
    * Deletes a BinaryObject from the object store.
    *
    * @param collection The UID of the collection.
    * @param key The binary object key.
    * @return Done.
    */
   CompletionStage<Done> deleteObject(UID collection, String key);

   /**
    * Reads a BinaryObject from the object store.
    *
    * @param collection The UID of the collection.
    * @param key The binary object key.
    * @return The binary object read from the object store.
    */
   CompletionStage<Optional<BinaryObject>> readObject(UID collection, String key);

}
