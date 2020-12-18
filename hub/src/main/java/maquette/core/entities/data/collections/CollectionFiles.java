package maquette.core.entities.data.collections;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.collections.exceptions.CollectionNotFoundException;
import maquette.core.entities.data.collections.exceptions.FileNotFoundException;
import maquette.core.entities.data.collections.exceptions.TagAlreadyExistsException;
import maquette.core.entities.data.collections.exceptions.TagNotFoundException;
import maquette.core.entities.data.collections.model.CollectionProperties;
import maquette.core.entities.data.collections.model.CollectionTag;
import maquette.core.entities.data.collections.model.FileEntry;
import maquette.core.ports.CollectionsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.binary.BinaryObject;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class CollectionFiles {

   private final UID id;

   private final CollectionsRepository repository;

   public CompletionStage<Done> put(User executor, BinaryObject data, String file, String message) {
      return remove(executor, file).thenCompose(done -> {
         var hash = Operators.randomHash();
         var key = String.format("collections/%s/%s", id.getValue(), hash);

         var insertCS = repository.saveObject(key, data);
         var updateFilesCS = getProperties()
            .thenApply(p -> {
               var files = p
                  .getFiles()
                  .withFile(file, FileEntry.RegularFile.apply(key, data.getSize(), message, ActionMetadata.apply(executor)));

               return p
                  .withFiles(files)
                  .withUpdated(ActionMetadata.apply(executor));
            });

         return Operators.compose(insertCS, updateFilesCS, (insert, updateFile) -> Done.getInstance());
      });
   }

   public CompletionStage<BinaryObject> read(User executor, String file) {
      return getProperties()
         .thenApply(p -> p.getFiles().getFile(file))
         .thenApply(maybeFile -> maybeFile.orElseThrow(() -> FileNotFoundException.withName(file)).getKey())
         .thenCompose(repository::readObject)
         .thenApply(maybeObject -> maybeObject.orElseThrow(() -> FileNotFoundException.withName(file)));
   }

   public CompletionStage<BinaryObject> read(User executor, String tag, String file) {
      return repository
         .findTagByName(id, tag)
         .thenApply(maybeTag -> maybeTag.orElseThrow(() -> TagNotFoundException.withName(tag)))
         .thenApply(t -> t.getContent().getFile(file))
         .thenApply(maybeFile -> maybeFile.orElseThrow(() -> FileNotFoundException.withName(file)))
         .thenCompose(f -> repository.readObject(f.getKey()))
         .thenApply(maybeObject -> maybeObject.orElseThrow(() -> FileNotFoundException.withName(file)));
   }

   public CompletionStage<Done> remove(User executor, String file) {
      return getProperties().thenCompose(p -> {
         var maybeFile = p.getFiles().getFile(file);

         if (maybeFile.isEmpty()) {
            return CompletableFuture.completedFuture(Done.getInstance());
         } else {
            var nextProperties = p
               .withFiles(p.getFiles().withoutFile(file))
               .withUpdated(ActionMetadata.apply(executor));

            return repository
               .findAllTags(id)
               .thenCompose(tags -> {
                  var isTaggedFile = tags
                     .stream()
                     .anyMatch(collectionTag -> collectionTag.getContent().getFile(file).isPresent());

                  if (isTaggedFile) {
                     return CompletableFuture.completedFuture(Done.getInstance());
                  } else {
                     return repository.deleteObject(maybeFile.get().getKey());
                  }
               })
               .thenCompose(done -> repository.insertOrUpdateAsset(nextProperties));
         }
      });
   }

   public CompletionStage<Done> tag(User executor, String name, String message) {
      // TODO validate name
      var existingTagCS = repository.findTagByName(id, name);
      var propertiesCS = getProperties();

      return Operators
         .compose(existingTagCS, propertiesCS, (existingTag, properties) -> {
            if (existingTag.isPresent()) {
               return CompletableFuture.<Done>failedFuture(TagAlreadyExistsException.withName(name));
            } else {
               var tag = CollectionTag.apply(ActionMetadata.apply(executor), name, message, properties.getFiles());
               var insert = repository.insertOrUpdateTag(id, tag);
               var updated = repository.insertOrUpdateAsset(properties.withUpdated(ActionMetadata.apply(executor)));

               return Operators.compose(insert, updated, (i, u) -> Done.getInstance());
            }
         })
         .thenCompose(d -> d);
   }

   public CompletionStage<List<CollectionTag>> getTags() {
      return repository.findAllTags(id);
   }

   private CompletionStage<CollectionProperties> getProperties() {
      return repository
         .findAssetById(id)
         .thenApply(maybeProperties -> {
            if (maybeProperties.isPresent()) {
               return maybeProperties.get();
            } else {
               throw CollectionNotFoundException.withId(id);
            }
         });
   }

}
