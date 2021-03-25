package maquette.adapters.collections;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.asset_providers.collections.model.CollectionTag;
import maquette.common.Operators;
import maquette.core.values.UID;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class FileSystemCollectionTagsCompanion {

   private static final String PATH = "tags";

   private static final String FILE_ENDING = ".tag.json";

   private final Path directory;

   private final ObjectMapper om;

   private Path getAssetDirectory(UID asset) {
      var file = directory
         .resolve(asset.getValue())
         .resolve(PATH);

      Operators.suppressExceptions(() -> Files.createDirectories(file));

      return file;
   }

   private Path getTagFile(UID asset, String tag) {
      return getAssetDirectory(asset).resolve(tag + FILE_ENDING);
   }

   public CompletionStage<List<CollectionTag>> findAllTags(UID collection) {
      var result = Operators
         .suppressExceptions(() -> Files.list(getAssetDirectory(collection)))
         .filter(file -> file.toString().endsWith(FILE_ENDING))
         .map(file -> Operators.suppressExceptions(() -> om.readValue(file.toFile(), CollectionTag.class)))
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   public CompletionStage<Optional<CollectionTag>> findTagByName(UID collection, String name) {
      return findAllTags(collection)
         .thenApply(tags -> tags
            .stream()
            .filter(tag -> tag.getName().equals(name))
            .findFirst());
   }

   public CompletionStage<Done> insertOrUpdateTag(UID collection, CollectionTag tag) {
      var file = getTagFile(collection, tag.getName());
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), tag));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

}
