package maquette.datashop.providers.collections.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.values.binary.BinaryObject;
import maquette.core.values.user.User;
import maquette.datashop.services.DataAssetServicesCompanion;
import maquette.datashop.values.access.DataAssetPermissions;

import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class CollectionServicesSecured implements CollectionServices {

    private final CollectionServices delegate;

    private final DataAssetServicesCompanion comp;

    @Override
    public CompletionStage<List<String>> listFiles(User executor, String collection, String tag) {
        return comp
            .withAuthorization(() -> comp.hasPermission(executor, collection, DataAssetPermissions::canConsume))
            .thenCompose(ok -> delegate.listFiles(executor, collection, tag));
    }

    @Override
    public CompletionStage<Done> put(User executor, String collection, BinaryObject data, String file, String message) {
        return comp
            .withAuthorization(() -> comp.hasPermission(executor, collection, DataAssetPermissions::canProduce))
            .thenCompose(ok -> delegate.put(executor, collection, data, file, message));
    }

    @Override
    public CompletionStage<Done> putAll(User executor, String collection, BinaryObject data, String basePath,
                                        String message) {
        return comp
            .withAuthorization(() -> comp.hasPermission(executor, collection, DataAssetPermissions::canProduce))
            .thenCompose(ok -> delegate.putAll(executor, collection, data, basePath, message));
    }

    @Override
    public CompletionStage<BinaryObject> readAll(User executor, String collection) {
        return comp
            .withAuthorization(() -> comp.hasPermission(executor, collection, DataAssetPermissions::canConsume))
            .thenCompose(ok -> delegate.readAll(executor, collection));
    }

    @Override
    public CompletionStage<BinaryObject> readAll(User executor, String collection, String tag) {
        return comp
            .withAuthorization(() -> comp.hasPermission(executor, collection, DataAssetPermissions::canConsume))
            .thenCompose(ok -> delegate.readAll(executor, collection, tag));
    }

    @Override
    public CompletionStage<BinaryObject> read(User executor, String collection, String file) {
        return comp
            .withAuthorization(() -> comp.hasPermission(executor, collection, DataAssetPermissions::canConsume))
            .thenCompose(ok -> delegate.read(executor, collection, file));
    }

    @Override
    public CompletionStage<BinaryObject> read(User executor, String collection, String tag, String file) {
        return comp
            .withAuthorization(() -> comp.hasPermission(executor, collection, DataAssetPermissions::canConsume))
            .thenCompose(ok -> delegate.read(executor, collection, tag, file));
    }

    @Override
    public CompletionStage<Done> remove(User executor, String collection, String file) {
        return comp
            .withAuthorization(() -> comp.hasPermission(executor, collection, DataAssetPermissions::canProduce))
            .thenCompose(ok -> delegate.remove(executor, collection, file));
    }

    @Override
    public CompletionStage<Done> tag(User executor, String collection, String tag, String message) {
        return comp
            .withAuthorization(() -> comp.hasPermission(executor, collection, DataAssetPermissions::canProduce))
            .thenCompose(ok -> delegate.tag(executor, collection, tag, message));
    }

}
