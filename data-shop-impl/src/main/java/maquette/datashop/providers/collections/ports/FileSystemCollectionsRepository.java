package maquette.datashop.providers.collections.ports;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.UID;
import maquette.core.values.binary.BinaryObject;
import maquette.datashop.configuration.FileSystemRepositoryConfiguration;
import maquette.datashop.ports.ObjectStore;
import maquette.datashop.providers.collections.model.CollectionTag;
import maquette.datashop.providers.collections.model.FileEntry;

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

    /**
     * Constructor which creates a new FileSystemCollectionsRepository object based on the given configuration and
     * ObjectMapper.
     *
     * @param config The configuration object.
     * @param om     The ObjectMapper which serializes Java objects into JSON.
     * @return The created object.
     */
    public static FileSystemCollectionsRepository apply(FileSystemRepositoryConfiguration config, ObjectMapper om) {
        var directory = config
            .getDirectory()
            .resolve("shop");
        var tagsCompanion = FileSystemCollectionTagsCompanion.apply(directory, om);

        return new FileSystemCollectionsRepository(directory, om, tagsCompanion);
    }


    /**
     * Returns the path of a collection.
     * Creates the directory if it did not exist before.
     *
     * @param collection The UID of the collection.
     * @return The path of the collection.
     */
    private Path getAssetDirectory(UID collection) {
        var dir = directory.resolve(collection.getValue());
        Operators.suppressExceptions(() -> Files.createDirectories(dir));
        return dir;
    }

    /**
     * Returns the path of a collection object store.
     * Creates the directory if it did not exist before.
     *
     * @param collection The UID of the collection.
     * @return The path of the collection object store.
     */
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
