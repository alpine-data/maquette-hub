package maquette.datashop.entities;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.user.User;
import maquette.datashop.exceptions.DataAssetAlreadyExistsException;
import maquette.datashop.exceptions.InvalidCustomSettingsException;
import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.providers.DataAssetProviders;
import maquette.datashop.values.DataAssetProperties;
import maquette.datashop.values.DataAssetState;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.access_requests.DataAccessRequestProperties;
import maquette.datashop.values.metadata.DataAssetMetadata;
import maquette.datashop.values.metadata.DataZone;
import maquette.datashop.values.metadata.PersonalInformation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DataAssetEntities {

    private final DataAssetsRepository repository;

    private final DataAssetProviders providers;

    public CompletionStage<DataAssetProperties> create(
        User executor, String type, DataAssetMetadata metadata, Authorization owner, Authorization steward,
        @Nullable Object customSettings) {

        return repository
            .findDataAssetByName(metadata.getName())
            .thenCompose(optEntity -> {
                if (optEntity.isPresent()) {
                    // TODO mw: Check for idempotent message from same.
                    return CompletableFuture.failedFuture(DataAssetAlreadyExistsException.withName(metadata.getName()));
                } else if (customSettings != null && !providers.getByName(type)
                    .getSettingsType()
                    .isInstance(customSettings)) {
                    return CompletableFuture.failedFuture(InvalidCustomSettingsException.apply(
                        type, customSettings.getClass(), providers.getByName(type).getSettingsType()));
                } else {
                    return CompletableFuture.completedFuture(Done.getInstance());
                }
            })
            .thenCompose(checked -> {
                var state = DataAssetState.APPROVED;

                if (
                    metadata.getZone().equals(DataZone.PREPARED) ||
                        metadata.getZone().equals(DataZone.GOLD) ||
                        metadata.getPersonalInformation().equals(PersonalInformation.PERSONAL_INFORMATION) ||
                        metadata.getPersonalInformation().equals(PersonalInformation.SENSITIVE_PERSONAL_INFORMATION)) {

                    state = DataAssetState.REVIEW_REQUIRED;
                }

                var created = ActionMetadata.apply(executor);
                var properties = DataAssetProperties.apply(UID.apply(), type, metadata, state, created, created);

                return repository
                    .insertOrUpdateDataAsset(properties)
                    .thenApply(d -> getById(properties.getId()))
                    .thenCompose(entity -> entity.getMembers()
                        .addMember(executor, owner, DataAssetMemberRole.OWNER)
                        .thenApply(d -> entity))
                    .thenCompose(entity -> entity.getMembers()
                        .addMember(executor, steward, DataAssetMemberRole.STEWARD)
                        .thenApply(d -> entity))
                    .thenCompose(entity -> {
                        var insertPropertiesCS = entity.updateCustomProperties(providers.getByName(type)
                            .getDefaultProperties());
                        var insertSettingsCS = entity.updateCustomSettings(executor, Optional
                            .ofNullable(customSettings)
                            .orElse(providers.getByName(type).getDefaultSettings()));

                        return Operators.compose(
                            insertPropertiesCS, insertSettingsCS,
                            (insertProperties, insertSettings) -> entity);
                    })
                    .thenCompose(entity -> {
                        var provider = providers.getByName(type);
                        return provider.onCreated(entity, customSettings);
                    })
                    .thenApply(d -> properties);
            });
    }

    public DataAssetEntity getById(UID id) {
        return DataAssetEntity.apply(id, repository, providers);
    }

    public CompletionStage<DataAssetEntity> getByName(String name) {
        return repository
            .getDataAssetByName(name)
            .thenApply(properties -> DataAssetEntity.apply(properties.getId(), repository, providers));
    }

    public CompletionStage<List<DataAssetProperties>> list() {
        return repository.listDataAssets().thenApply(s -> s.collect(Collectors.toList()));
    }

    public CompletionStage<Done> removeByName(String name) {
        return repository.removeDataAssetByName(name);
    }

    public CompletionStage<List<DataAccessRequestProperties>> getDataAccessRequestsByWorkspace(UID workspace) {
        return repository
            .findDataAccessRequestsByWorkspace(workspace);
    }

    public CompletionStage<List<DataAssetProperties>> getDataAssetsByWorkspace(UID workspace) {
        return getDataAccessRequestsByWorkspace(workspace).thenCompose(requests -> Operators.allOf(requests
            .stream()
            .map(DataAccessRequestProperties::getAsset)
            .map(asset -> getById(asset).getProperties())));
    }

}
