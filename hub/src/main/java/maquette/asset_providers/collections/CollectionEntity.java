package maquette.asset_providers.collections;

import akka.Done;
import akka.japi.Pair;
import lombok.AllArgsConstructor;
import maquette.asset_providers.collections.exceptions.FileNotFoundException;
import maquette.asset_providers.collections.exceptions.TagAlreadyExistsException;
import maquette.asset_providers.collections.exceptions.TagNotFoundException;
import maquette.asset_providers.collections.model.CollectionTag;
import maquette.asset_providers.collections.model.FileEntry;
import maquette.common.Operators;
import maquette.core.entities.data.DataAssetEntity;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.binary.BinaryObject;
import maquette.core.values.data.binary.BinaryObjects;
import maquette.core.values.user.User;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@AllArgsConstructor(staticName = "apply")
public final class CollectionEntity {

   private static final Logger LOG = LoggerFactory.getLogger(CollectionEntity.class);

   private final UID id;

   private final CollectionsRepository repository;

   private final DataAssetEntity entity;

   public CompletionStage<List<String>> list() {
      return repository.getFiles(id).thenApply(FileEntry.Directory::fileNames);
   }

   public CompletionStage<List<String>> list(String tag) {
      if (tag.equals("main")) {
         return list();
      } else {
         return repository
            .findTagByName(id, tag)
            .thenApply(maybeTag -> maybeTag.orElseThrow(() -> TagNotFoundException.withName(tag)))
            .thenApply(t -> t.getContent().fileNames());
      }
   }

   /**
    * Add or update a single file to the collection.
    *
    * @param executor The user who executes the action.
    * @param data     The data for the file.
    * @param file     The filename (including path).
    * @param message  Some message describing the update.
    * @return Done
    */
   public CompletionStage<Done> put(User executor, BinaryObject data, String file, String message) {
      return remove(executor, file).thenCompose(done -> {
         var hash = Operators.randomHash();
         var key = String.format("collections/%s/%s", id.getValue(), hash);

         var insertCS = repository.saveObject(key, data);
         var updateFilesCS = repository
            .getFiles(id)
            .thenApply(f -> f.withFile(file, FileEntry.RegularFile.apply(key, data.getSize(), mapFilenameToFileType(file), message, ActionMetadata.apply(executor))))
            .thenCompose(files -> repository.saveFiles(id, files))
            .thenCompose(d -> entity.updated(executor));

         return Operators.compose(insertCS, updateFilesCS, (insert, updateFile) -> Done.getInstance());
      });
   }

   /**
    * Add or update a set of files packaged in a zip file.
    *
    * @param executor The user who uploads the data.
    * @param data     The zip file.
    * @param message  A message for inserting the data.
    * @return Done
    */
   public CompletionStage<Done> putAll(User executor, BinaryObject data, String basePath, String message) {
      return Operators.suppressExceptions(() -> {
         var result = CompletableFuture.completedFuture(Done.getInstance());

         try (var zis = new ZipInputStream(data.toInputStream())) {
            var zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
               if (!zipEntry.isDirectory()) {
                  var bin = BinaryObjects.fromInputStream(zis);
                  var name = zipEntry.getName();

                  result = result
                     .thenCompose(d -> put(executor, bin, basePath + "/" + name, message))
                     .thenApply(done -> {
                        bin.discard();
                        return done;
                     });
               }

               zipEntry = zis.getNextEntry();
            }
         }

         return result;
      });
   }

   /**
    * Reads all files of the collection from the latest state.
    *
    * @param executor The user who executes the action.
    * @return A zip file including all files.
    */
   public CompletionStage<BinaryObject> readAll(User executor) {
      return repository
         .getFiles(id)
         .thenApply(files -> files
            .files()
            .stream()
            .map(file -> repository
               .readObject(file.getFile().getKey())
               .thenApply(obj -> Pair.apply(file, obj))))
         .thenCompose(Operators::allOf)
         .thenApply(objects -> objects
            .stream()
            .filter(pair -> pair.second().isPresent())
            .map(pair -> Pair.apply(pair.first(), pair.second().orElse(BinaryObjects.empty())))
            .collect(Collectors.toList()))
         .thenApply(this::createZipFile);
   }

   public CompletionStage<BinaryObject> readAll(User executor, String tag) {
      if (tag.equals("main")) {
         return readAll(executor);
      } else {
         return repository
            .findTagByName(id, tag)
            .thenApply(maybeTag -> maybeTag.orElseThrow(() -> TagNotFoundException.withName(tag)))
            .thenApply(collectionTag -> collectionTag
               .getContent()
               .files()
               .stream()
               .map(file -> repository
                  .readObject(file.getFile().getKey())
                  .thenApply(obj -> Pair.apply(file, obj))))
            .thenCompose(Operators::allOf)
            .thenApply(objects -> objects
               .stream()
               .filter(pair -> pair.second().isPresent())
               .map(pair -> Pair.apply(pair.first(), pair.second().orElse(BinaryObjects.empty())))
               .collect(Collectors.toList()))
            .thenApply(this::createZipFile);
      }
   }

   private BinaryObject createZipFile(List<Pair<FileEntry.NamedRegularFile, BinaryObject>> files) {
      var zipFile = Operators.suppressExceptions(() -> Files.createTempFile("mq", "zip"));

      try (
         var fos = new FileOutputStream(zipFile.toFile());
         var zos = new ZipOutputStream(fos)) {

         for (var pair : files) {
            var entry = new ZipEntry(pair.first().getName());
            var fis = pair.second().toInputStream();

            zos.putNextEntry(entry);

            byte[] bytes = new byte[1024];
            while (fis.read(bytes) >= 0) {
               zos.write(bytes);
            }

            fis.close();
         }
      } catch (IOException e) {
         LOG.warn(String.format("Exception occurred while creating zip file for collection `%s`", id), e);
      }

      return BinaryObjects.fromTemporaryFile(zipFile);
   }

   public CompletionStage<BinaryObject> read(User executor, String file) {
      return repository
         .getFiles(id)
         .thenApply(files -> files.getFile(file))
         .thenApply(maybeFile -> maybeFile.orElseThrow(() -> FileNotFoundException.withName(file)).getKey())
         .thenCompose(repository::readObject)
         .thenApply(maybeObject -> maybeObject.orElseThrow(() -> FileNotFoundException.withName(file)));
   }

   public CompletionStage<BinaryObject> read(User executor, String tag, String file) {
      if (tag.equals("main")) {
         return read(executor, file);
      } else {
         return repository
            .findTagByName(id, tag)
            .thenApply(maybeTag -> maybeTag.orElseThrow(() -> TagNotFoundException.withName(tag)))
            .thenApply(t -> t.getContent().getFile(file))
            .thenApply(maybeFile -> maybeFile.orElseThrow(() -> FileNotFoundException.withName(file)))
            .thenCompose(f -> repository.readObject(f.getKey()))
            .thenApply(maybeObject -> maybeObject.orElseThrow(() -> FileNotFoundException.withName(file)));
      }
   }

   public CompletionStage<Done> remove(User executor, String file) {
      return repository
         .getFiles(id)
         .thenCompose(files -> {
            var maybeFile = files.getFile(file);

            if (maybeFile.isEmpty()) {
               return CompletableFuture.completedFuture(Done.getInstance());
            } else {
               var nextFiles = files.withoutFile(file);

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
                  .thenCompose(done -> repository.saveFiles(id, nextFiles))
                  .thenCompose(done -> entity.updated(executor));
            }
         });
   }

   public CompletionStage<Done> tag(User executor, String name, String message) {
      if (name.equals("main")) {
         return CompletableFuture.failedFuture(TagAlreadyExistsException.withName(name));
      } else {
         var existingTagCS = repository.findTagByName(id, name);
         var propertiesCS = entity.getProperties();
         var filesCS = repository.getFiles(id);

         return Operators
            .compose(existingTagCS, propertiesCS, filesCS, (existingTag, properties, files) -> {
               if (existingTag.isPresent()) {
                  return CompletableFuture.<Done>failedFuture(TagAlreadyExistsException.withName(name));
               } else {
                  entity.getCustomSettings(FileEntry.Directory.class);
                  var tag = CollectionTag.apply(ActionMetadata.apply(executor), name, message, files);
                  var insert = repository.insertOrUpdateTag(id, tag);
                  var updated = entity.updated(executor);

                  return Operators.compose(insert, updated, (i, u) -> Done.getInstance());
               }
            })
            .thenCompose(d -> d);
      }
   }

   public CompletionStage<List<CollectionTag>> getTags() {
      return repository.findAllTags(id);
   }

   private FileEntry.FileType mapFilenameToFileType(String filename) {
      var ext = FilenameUtils.getExtension(filename);

      switch (ext.toLowerCase()) {
         case "jpg":
         case "png":
         case "gif":
            return FileEntry.FileType.IMAGE;
         case "txt":
         case "py":
         case "json":
         case "java":
         case "xml":
         case "md":
            return FileEntry.FileType.TEXT;
         default:
            return FileEntry.FileType.BINARY;
      }
   }
}
