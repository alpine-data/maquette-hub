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

   CompletionStage<List<CollectionTag>> findAllTags(UID collection);

   CompletionStage<Optional<CollectionTag>> findTagByName(UID collection, String name);

   CompletionStage<Done> insertOrUpdateTag(UID collection, CollectionTag tag);

   CompletionStage<Done> saveFiles(UID collection, FileEntry.Directory files);

   CompletionStage<FileEntry.Directory> getFiles(UID collection);

   CompletionStage<Done> saveObject(UID collection, String key, BinaryObject binary);

   CompletionStage<Done> deleteObject(UID collection, String key);

   CompletionStage<Optional<BinaryObject>> readObject(UID collection, String key);

}
