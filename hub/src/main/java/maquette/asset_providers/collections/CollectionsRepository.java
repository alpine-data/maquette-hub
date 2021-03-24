package maquette.asset_providers.collections;

import akka.Done;
import maquette.asset_providers.collections.model.CollectionTag;
import maquette.asset_providers.collections.model.FileEntry;
import maquette.core.ports.ObjectStore;
import maquette.core.values.UID;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface CollectionsRepository extends ObjectStore {

   CompletionStage<List<CollectionTag>> findAllTags(UID collection);

   CompletionStage<Optional<CollectionTag>> findTagByName(UID collection, String name);

   CompletionStage<Done> insertOrUpdateTag(UID collection, CollectionTag tag);

   CompletionStage<Done> saveFiles(UID collection, FileEntry.Directory files);

   CompletionStage<FileEntry.Directory> getFiles(UID collection);

}
