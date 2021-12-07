package maquette.datashop.ports;

import akka.Done;
import akka.japi.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.common.Operators;
import maquette.core.ports.InMemoryMembersRepository;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.datashop.exceptions.DataAssetNotFoundException;
import maquette.datashop.values.DataAssetProperties;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.access_requests.DataAccessRequestProperties;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An in-memory implementation of the {@link DataAssetsRepository} to showcase expected behavior and for
 * simple tests.
 */
@AllArgsConstructor(staticName = "apply")
public final class InMemoryDataAssetsRepository implements DataAssetsRepository {

    private final InMemoryMembersRepository<DataAssetMemberRole> members;

    private final Map<UID, DataAssetPersisted> store;

    public static InMemoryDataAssetsRepository apply() {
        return apply(InMemoryMembersRepository.apply(), Maps.newHashMap());
    }

    @Override
    public CompletionStage<List<GrantedAuthorization<DataAssetMemberRole>>> findAllMembers(UID parent) {
        return members.findAllMembers(parent);
    }

    @Override
    public CompletionStage<List<GrantedAuthorization<DataAssetMemberRole>>> findMembersByRole(UID parent,
                                                                                              DataAssetMemberRole role) {
        return members.findMembersByRole(parent, role);
    }

    @Override
    public CompletionStage<Done> insertOrUpdateMember(UID parent, GrantedAuthorization<DataAssetMemberRole> member) {
        return members.insertOrUpdateMember(parent, member);
    }

    @Override
    public CompletionStage<Done> removeMember(UID parent, Authorization member) {
        return members.removeMember(parent, member);
    }

    @Override
    public CompletionStage<Optional<DataAssetProperties>> findDataAssetByName(String name) {
        var result = store
            .values()
            .stream()
            .map(p -> p.properties)
            .filter(p -> p.getMetadata().getName().equals(name))
            .findFirst();

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Optional<DataAssetProperties>> findDataAssetById(UID id) {
        var result = Optional.ofNullable(store.get(id)).map(p -> p.properties);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> CompletionStage<Optional<T>> fetchCustomSettings(UID id, Class<T> expectedType) {
        var result = Optional.ofNullable(store.get(id)).flatMap(p -> Optional.ofNullable((T) p.customSettings));
        return CompletableFuture.completedFuture(result);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> CompletionStage<Optional<T>> fetchCustomProperties(UID id, Class<T> expectedType) {
        var result = Optional.ofNullable(store.get(id)).flatMap(p -> Optional.ofNullable((T) p.customProperties));
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Done> insertOrUpdateDataAsset(DataAssetProperties updated) {
        if (store.containsKey(updated.getId())) {
            store.put(updated.getId(), store.get(updated.getId()).withProperties(updated));
        } else {
            store.put(updated.getId(), DataAssetPersisted.apply(updated));
        }

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> insertOrUpdateCustomSettings(UID id, Object customSettings) {
        return updateDataAsset(id, asset -> asset.withCustomSettings(customSettings));
    }

    @Override
    public CompletionStage<Done> insertOrUpdateCustomProperties(UID id, Object customProperties) {
        return updateDataAsset(id, asset -> asset.withCustomProperties(customProperties));
    }

    @Override
    public CompletionStage<Stream<DataAssetProperties>> listDataAssets() {
        return CompletableFuture.completedFuture(store.values().stream().map(a -> a.properties));
    }

    @Override
    public CompletionStage<Done> removeDataAssetById(UID id) {
        store.remove(id);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Optional<DataAccessRequestProperties>> findDataAccessRequestById(UID asset, UID request) {
        var result = Optional.ofNullable(store.get(asset))
            .flatMap(a -> Optional.ofNullable(a.accessRequests.get(request)));
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Done> insertOrUpdateDataAccessRequest(DataAccessRequestProperties request) {
        return updateDataAsset(request.getAsset(), asset -> {
            asset.accessRequests.put(request.getId(), request);
            return asset;
        });
    }

    @Override
    public CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByWorkspace(UID workspace) {
        var result = store
            .values()
            .stream()
            .flatMap(asset -> asset.accessRequests.values().stream())
            .filter(request -> request.getWorkspace().equals(workspace))
            .collect(Collectors.toList());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByAsset(UID asset) {
        var result = Optional
            .ofNullable(store.get(asset))
            .map(a -> (List<DataAccessRequestProperties>) Lists.newArrayList(a.accessRequests.values()))
            .orElse(List.of());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Done> removeDataAccessRequest(UID asset, UID id) {
        return updateDataAsset(asset, a -> {
            a.accessRequests.remove(id);
            return a;
        });
    }

    private CompletionStage<Done> updateDataAsset(UID id, Function<DataAssetPersisted, DataAssetPersisted> updater) {
        if (!store.containsKey(id)) {
            return CompletableFuture.failedFuture(DataAssetNotFoundException.applyFromId(id));
        }

        store.put(id, Operators.suppressExceptions(() -> updater.apply(store.get(id))));
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @With
    @Value
    @AllArgsConstructor(staticName = "apply")
    private static class DataAssetPersisted {

        DataAssetProperties properties;

        Object customSettings;

        Object customProperties;

        Map<UID, DataAccessRequestProperties> accessRequests;

        static DataAssetPersisted apply(DataAssetProperties properties) {
            return apply(properties, null, null, Maps.newHashMap());
        }

    }

}
