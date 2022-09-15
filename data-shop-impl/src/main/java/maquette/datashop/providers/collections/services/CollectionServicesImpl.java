package maquette.datashop.providers.collections.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.values.binary.BinaryObject;
import maquette.core.values.user.User;
import maquette.datashop.entities.DataAssetEntities;
import maquette.datashop.providers.collections.CollectionEntity;
import maquette.datashop.providers.collections.ports.CollectionsRepository;

import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class CollectionServicesImpl implements CollectionServices {

    private final DataAssetEntities entities;

    private final CollectionsRepository repository;

    @Override
    public CompletionStage<List<String>> listFiles(User executor, String collection, String tag) {
        return get(collection).thenCompose(c -> c.list(tag));
    }

    @Override
    public CompletionStage<Done> put(User executor, String collection, BinaryObject data, String file, String message) {
        return get(collection).thenCompose(c -> c.put(executor, data, file, message));
    }

    @Override
    public CompletionStage<Done> putAll(User executor, String collection, BinaryObject data, String basePath,
                                        String message) {
        return get(collection).thenCompose(c -> c.putAll(executor, data, basePath, message));
    }

    @Override
    public CompletionStage<BinaryObject> readAll(User executor, String collection) {
        return get(collection).thenCompose(c -> c.readAll(executor));
    }

    @Override
    public CompletionStage<BinaryObject> readAll(User executor, String collection, String tag) {
        return get(collection).thenCompose(c -> c.readAll(executor, tag));
    }

    @Override
    public CompletionStage<BinaryObject> read(User executor, String collection, String file) {
        return get(collection).thenCompose(c -> c.read(executor, file));
    }

    @Override
    public CompletionStage<BinaryObject> read(User executor, String collection, String tag, String file) {
        return get(collection).thenCompose(c -> c.read(executor, tag, file));
    }

    @Override
    public CompletionStage<Done> remove(User executor, String collection, String file) {
        return get(collection).thenCompose(c -> c.remove(executor, file));
    }

    @Override
    public CompletionStage<Done> tag(User executor, String collection, String tag, String message) {
        return get(collection).thenCompose(c -> c.tag(executor, tag, message));
    }

    private CompletionStage<CollectionEntity> get(String collection) {
        return entities
            .getByName(collection)
            .thenApply(entity -> CollectionEntity.apply(entity.getId(), repository, entity));
    }

}
