package maquette.datashop.providers.collections;

import akka.Done;
import akka.japi.Pair;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.binary.BinaryObject;
import maquette.core.values.binary.BinaryObjects;
import maquette.core.values.user.User;
import maquette.datashop.entities.DataAssetEntity;
import maquette.datashop.providers.collections.exceptions.FileNotFoundException;
import maquette.datashop.providers.collections.exceptions.TagAlreadyExistsException;
import maquette.datashop.providers.collections.exceptions.TagNotFoundException;
import maquette.datashop.providers.collections.model.CollectionTag;
import maquette.datashop.providers.collections.model.FileEntry;
import maquette.datashop.providers.collections.ports.CollectionsRepository;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
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

    private final int concurrentRequests;

    /**
     * Lists the file paths of all files contained in the collection.
     *
     * @return The list of file paths.
     */
    public CompletionStage<List<String>> list() {
        return repository
            .getFiles(id)
            .thenApply(FileEntry.Directory::fileNames);
    }

    /**
     * Lists the file paths of all files belonging to the given tag in the collection.
     *
     * @param tag The name of the tag.
     * @return The list of file paths.
     */
    public CompletionStage<List<String>> list(String tag) {
        if (Objects.isNull(tag) || tag.equals("main")) {
            return list();
        } else {
            return repository
                .findTagByName(id, tag)
                .thenApply(maybeTag -> maybeTag.orElseThrow(() -> TagNotFoundException.withName(tag)))
                .thenApply(t -> t
                    .getContent()
                    .fileNames());
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
            var insertCS = repository.saveObject(id, hash, data);

            var updateFilesCS = repository
                .getFiles(id)
                .thenApply(f -> f.withFile(file,
                    FileEntry.RegularFile.apply(hash, data.getSize(), mapFilenameToFileType(file), message,
                        ActionMetadata.apply(executor))))
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
            var savedFiles = new ArrayList<CompletableFuture<CompletionStage<Pair<String, FileEntry.RegularFile>>>>();

            var es = Executors.newFixedThreadPool(concurrentRequests);
            var files = repository.getFiles(id).toCompletableFuture().get();

            try (var zis = new ZipInputStream(data.toInputStream())) {
                var zipEntry = zis.getNextEntry();

                while (zipEntry != null) {
                    if (!zipEntry.isDirectory()) {
                        var binaryObject = BinaryObjects.fromInputStream(zis);
                        var name = zipEntry.getName();

                        // remove leading folder to add basePath
                        name = basePath
                            .replaceAll("^/+", "")
                            .replaceAll("/+$", "") + "/" + name
                            .substring(name.indexOf("/", 1) + 1);

                        var hash = files.getFile(name)
                            .map(FileEntry.RegularFile::getKey)
                            .orElse(Operators.randomHash());

                        String finalName = name;
                        savedFiles.add(CompletableFuture
                            .supplyAsync(() -> repository
                                .saveObject(id, hash, binaryObject)
                                .thenApply(done -> Pair.apply(finalName, FileEntry.RegularFile.apply(
                                    hash, binaryObject.getSize(), mapFilenameToFileType(finalName), message,
                                    ActionMetadata.apply(executor)))), es)
                        );
                    }

                    zipEntry = zis.getNextEntry();
                }
            }

            return CompletableFuture
                .allOf(savedFiles.toArray(new CompletableFuture[savedFiles.size()]))
                .thenApply(done -> savedFiles
                    .stream()
                    .filter(Objects::nonNull)
                    .map(cf -> Operators.suppressExceptions(() -> cf.thenCompose(cs -> cs).get()))
                    .collect(Collectors.toList()))
                .thenApply(savedFilesList -> {
                    var filesUpdated = files;
                    for (var f : savedFilesList) {
                        filesUpdated = filesUpdated.withFile(f.first(), f.second());
                    }
                    return filesUpdated;
                })
                .thenApply(filesUpdated -> repository.saveFiles(id, filesUpdated))
                .thenCompose(done -> entity.updated(executor));
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
            .thenApply(files -> {
                var readFiles = new ArrayList<CompletableFuture<CompletionStage<Pair<FileEntry.NamedRegularFile, Optional<BinaryObject>>>>>();
                var es = Executors.newFixedThreadPool(concurrentRequests);
                files
                    .files()
                    .forEach(file -> readFiles.add(CompletableFuture
                        .supplyAsync(() -> repository
                            .readObject(id, file
                                .getFile()
                                .getKey())
                            .thenApply(obj -> Pair.apply(file, obj)), es)
                    ));

                return CompletableFuture
                    .allOf(readFiles.toArray(new CompletableFuture[readFiles.size()]))
                    .thenApply(done -> readFiles
                        .stream()
                        .filter(Objects::nonNull)
                        .map(cf -> Operators.suppressExceptions(() -> cf.thenCompose(cs -> cs).get()))
                        .filter(pair -> pair
                            .second()
                            .isPresent())
                        .map(pair -> Pair.apply(pair.first(), pair
                            .second()
                            .orElse(BinaryObjects.empty())))
                        .collect(Collectors.toList()))
                    .thenApply(this::createZipFile);
            })
            .thenCompose(cf -> cf);
    }

    /**
     * Reads all files belonging to the given tag in the collection.
     *
     * @param executor The user who executes the action.
     * @param tag      The name of the tag.
     * @return A zip file including all files.
     */
    public CompletionStage<BinaryObject> readAll(User executor, String tag) {
        if (tag.equals("main")) {
            return readAll(executor);
        } else {
            return repository
                .findTagByName(id, tag)
                .thenApply(maybeTag -> maybeTag.orElseThrow(() -> TagNotFoundException.withName(tag)))
                .thenApply(collectionTag -> {
                    var readFiles = new ArrayList<CompletableFuture<CompletionStage<Pair<FileEntry.NamedRegularFile, Optional<BinaryObject>>>>>();
                    var es = Executors.newFixedThreadPool(concurrentRequests);

                    collectionTag
                        .getContent()
                        .files()
                        .forEach(file -> readFiles.add(CompletableFuture
                            .supplyAsync(() -> repository
                                .readObject(id, file
                                    .getFile()
                                    .getKey())
                                .thenApply(obj -> Pair.apply(file, obj)), es)
                        ));

                    return CompletableFuture
                        .allOf(readFiles.toArray(new CompletableFuture[readFiles.size()]))
                        .thenApply(done -> readFiles
                            .stream()
                            .filter(Objects::nonNull)
                            .map(cf -> Operators.suppressExceptions(() -> cf.thenCompose(cs -> cs).get()))
                            .filter(pair -> pair
                                .second()
                                .isPresent())
                            .map(pair -> Pair.apply(pair.first(), pair
                                .second()
                                .orElse(BinaryObjects.empty())))
                            .collect(Collectors.toList()))
                        .thenApply(this::createZipFile);
                })
                .thenCompose(cf -> cf);
        }
    }

    /**
     * Creates a zip file containing multiple files.
     *
     * @param files The files to be zipped.
     * @return A zip file including the files.
     */
    private BinaryObject createZipFile(List<Pair<FileEntry.NamedRegularFile, BinaryObject>> files) {
        var zipFile = Operators.suppressExceptions(() -> Files.createTempFile("mq", "zip"));

        try (
            var fos = new FileOutputStream(zipFile.toFile());
            var zos = new ZipOutputStream(fos)) {

            for (var pair : files) {
                var entry = new ZipEntry(pair
                    .first()
                    .getName());
                var fis = pair
                    .second()
                    .toInputStream();

                zos.putNextEntry(entry);

                int length;
                byte[] bytes = new byte[1024];
                while ((length = fis.read(bytes)) > 0) {
                    zos.write(bytes, 0, length);
                }
                zos.closeEntry();

                fis.close();
            }
        } catch (IOException e) {
            LOG.warn(String.format("Exception occurred while creating zip file for collection `%s`", id), e);
        }

        return BinaryObjects.fromTemporaryFile(zipFile);
    }

    /**
     * Reads a single file from the object store.
     *
     * @param executor The user who executes the action.
     * @param file     The name of the file to be read.
     * @return The file read from the object store.
     */
    public CompletionStage<BinaryObject> read(User executor, String file) {
        return repository
            .getFiles(id)
            .thenApply(files -> files.getFile(file))
            .thenApply(maybeFile -> maybeFile
                .orElseThrow(() -> FileNotFoundException.withName(file))
                .getKey())
            .thenCompose(key -> repository.readObject(id, key))
            .thenApply(maybeObject -> maybeObject.orElseThrow(() -> FileNotFoundException.withName(file)));
    }

    /**
     * Reads a single file belonging to a given tag from the object store.
     *
     * @param executor The user who executes the action.
     * @param tag      The name of the tag.
     * @param file     The name of the file to be read.
     * @return The file read from the object store.
     */
    public CompletionStage<BinaryObject> read(User executor, String tag, String file) {
        if (tag.equals("main")) {
            return read(executor, file);
        } else {
            return repository
                .findTagByName(id, tag)
                .thenApply(maybeTag -> maybeTag.orElseThrow(() -> TagNotFoundException.withName(tag)))
                .thenApply(t -> t
                    .getContent()
                    .getFile(file))
                .thenApply(maybeFile -> maybeFile.orElseThrow(() -> FileNotFoundException.withName(file)))
                .thenCompose(f -> repository.readObject(id, f.getKey()))
                .thenApply(maybeObject -> maybeObject.orElseThrow(() -> FileNotFoundException.withName(file)));
        }
    }

    /**
     * Deletes all files contained in a directory from the object store.
     *
     * @param executor  The user who executes the action.
     * @param directory The path of the directory to be deleted.
     * @return Done.
     */
    public CompletionStage<Done> removeAll(User executor, String directory) {
        var deletedFiles = new ArrayList<CompletionStage<Done>>();
        return repository
            .getFiles(id)
            .thenCompose(files -> {
                var dir = files.getDirectory(directory);
                if (dir.isPresent()) {
                    System.out.println(dir.get().files());
                    for (FileEntry.NamedRegularFile file: dir.get().files()) {
                        deletedFiles.add(this.remove(executor, directory + "/" + file.getName()));
                    }
                }
                return Operators.allOf(deletedFiles);
            })
            .thenApply(done -> Done.getInstance());
    }

    /**
     * Deletes a single file from the object store.
     *
     * @param executor The user who executes the action.
     * @param file     The name of the file to be deleted.
     * @return Done.
     */
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
                                .anyMatch(collectionTag -> collectionTag
                                    .getContent()
                                    .getFile(file)
                                    .isPresent());

                            if (isTaggedFile) {
                                return CompletableFuture.completedFuture(Done.getInstance());
                            } else {
                                return repository.deleteObject(id, maybeFile
                                    .get()
                                    .getKey());
                            }
                        })
                        .thenCompose(done -> repository.saveFiles(id, nextFiles))
                        .thenCompose(done -> entity.updated(executor));
                }
            });
    }

    /**
     * Adds a new tag to the collection.
     *
     * @param executor The user who executes the action.
     * @param name     The name of the tag.
     * @param message  The message describing the action.
     * @return Done.
     */
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

    /**
     * Maps file names to file types.
     *
     * @param filename The name of the file to be mapped to a file type.
     * @return The type of the file.
     */
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
