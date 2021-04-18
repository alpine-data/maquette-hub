package maquette.adapters.collections;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.asset_providers.collections.CollectionsRepository;
import maquette.asset_providers.collections.model.CollectionTag;
import maquette.asset_providers.collections.model.FileEntry;
import maquette.common.Operators;
import maquette.config.FileSystemRepositoryConfiguration;
import maquette.core.ports.ObjectStore;
import maquette.core.values.UID;
import maquette.core.values.data.binary.BinaryObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileSystemCollectionsRepository implements CollectionsRepository {

   private static final String FILES = "files.json";

   private final Path directory;

   private final ObjectMapper om;

   private final FileSystemCollectionTagsCompanion tagsCompanion;

   public static FileSystemCollectionsRepository apply(FileSystemRepositoryConfiguration config, ObjectMapper om) {
      var directory = config.getDirectory().resolve("shop");
      var tagsCompanion = FileSystemCollectionTagsCompanion.apply(directory, om);

      return new FileSystemCollectionsRepository(directory, om, tagsCompanion);
   }

   private Path getAssetDirectory(UID collection) {
      var dir = directory.resolve(collection.getValue());
      Operators.suppressExceptions(() -> Files.createDirectories(dir));
      return dir;
   }

   private ObjectStore getObjectStore(UID collection) {
      var dir = getAssetDirectory(collection).resolve("objects");
      Operators.suppressExceptions(() -> Files.createDirectories(dir));
      return FileSystemObjectsStore.apply(dir);
   }

   @Override
   public CompletionStage<List<CollectionTag>> findAllTags(UID collection) {
      return tagsCompanion.findAllTags(collection);
   }

   @Override
   public CompletionStage<Optional<CollectionTag>> findTagByName(UID collection, String name) {
      return tagsCompanion.findTagByName(collection, name);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateTag(UID collection, CollectionTag tag) {
      return tagsCompanion.insertOrUpdateTag(collection, tag);
   }

   @Override
   public CompletionStage<Done> saveFiles(UID collection, FileEntry.Directory files) {
      var file = getAssetDirectory(collection).resolve(FILES);
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), files));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<FileEntry.Directory> getFiles(UID collection) {
      var file = getAssetDirectory(collection).resolve(FILES);

      if (Files.exists(file)) {
         var result = Operators.suppressExceptions(() -> om.readValue(file.toFile(), FileEntry.Directory.class));
         return CompletableFuture.completedFuture(result);
      } else {
         return CompletableFuture.completedFuture(FileEntry.Directory.apply());
      }
   }

   @Override
   public CompletionStage<Done> saveObject(UID collection, String key, BinaryObject binary) {
      return getObjectStore(collection).saveObject(key, binary);
   }

   @Override
   public CompletionStage<Done> deleteObject(UID collection, String key) {
      return getObjectStore(collection).deleteObject(key);
   }

   @Override
   public CompletionStage<Optional<BinaryObject>> readObject(UID collection, String key) {
      return getObjectStore(collection).readObject(key);
   }

}
