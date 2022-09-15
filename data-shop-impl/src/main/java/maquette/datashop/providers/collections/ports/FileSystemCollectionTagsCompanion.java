package maquette.datashop.providers.collections.ports;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.UID;
import maquette.datashop.providers.collections.model.CollectionTag;

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

    /**
     * Returns the path of a collection.
     * Creates the directory if it did not exist before.
     *
     * @param collection The UID of the collection.
     * @return The path of the collection.
     */
    private Path getAssetDirectory(UID collection) {
        var file = directory
            .resolve(collection.getValue())
            .resolve(PATH);

        Operators.suppressExceptions(() -> Files.createDirectories(file));

        return file;
    }

    /**
     * Returns the path of the tag file contained in the collection directory.
     *
     * @param collection The UID of the collection.
     * @param tag        The name of the tag.
     * @return The path of the tag file.
     */
    private Path getTagFile(UID collection, String tag) {
        return getAssetDirectory(collection).resolve(tag + FILE_ENDING);
    }

    /**
     * Finds and returns all tags of a collection.
     *
     * @param collection The UID of the collection.
     * @return The tags of the collection.
     */
    public CompletionStage<List<CollectionTag>> findAllTags(UID collection) {
        var result = Operators
            .suppressExceptions(() -> Files.list(getAssetDirectory(collection)))
            .filter(file -> file
                .toString()
                .endsWith(FILE_ENDING))
            .map(file -> Operators.suppressExceptions(() -> om.readValue(file.toFile(), CollectionTag.class)))
            .collect(Collectors.toList());

        return CompletableFuture.completedFuture(result);
    }

    /**
     * Finds a tag of a collection by the tag name.
     *
     * @param collection The UID of the collection.
     * @param name       The name of the tag.
     * @return The first resolved tag.
     */
    public CompletionStage<Optional<CollectionTag>> findTagByName(UID collection, String name) {
        return findAllTags(collection)
            .thenApply(tags -> tags
                .stream()
                .filter(tag -> tag
                    .getName()
                    .equals(name))
                .findFirst());
    }

    /**
     * Persist a tag object.
     *
     * @param collection The UID of the collection.
     * @param tag        The tag object.
     * @return Done.
     */
    public CompletionStage<Done> insertOrUpdateTag(UID collection, CollectionTag tag) {
        var file = getTagFile(collection, tag.getName());
        Operators.suppressExceptions(() -> om.writeValue(file.toFile(), tag));
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
