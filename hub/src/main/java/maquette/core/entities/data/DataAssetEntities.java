package maquette.core.entities.data;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.exceptions.DataAssetAlreadyExistsException;
import maquette.core.entities.data.exceptions.InvalidCustomSettingsException;
import maquette.core.entities.data.exceptions.UnknownDataAssetTypeException;
import maquette.core.entities.data.model.DataAssetMetadata;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.entities.data.ports.DataAssetsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.data.DataAssetState;
import maquette.core.values.data.DataZone;
import maquette.core.values.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataAssetEntities {

   private final DataAssetsRepository repository;

   private final Map<String, DataAssetProvider> providers;

   public CompletionStage<DataAssetProperties> create(
      User executor, String type, DataAssetMetadata metadata, Authorization owner, Authorization steward, @Nullable Object customSettings) {

      return repository
         .findEntityByName(metadata.getName())
         .thenCompose(optEntity -> {
            if (optEntity.isPresent()) {
               return CompletableFuture.failedFuture(DataAssetAlreadyExistsException.withName(metadata.getName()));
            } else if (!providers.containsKey(type)) {
               return CompletableFuture.failedFuture(UnknownDataAssetTypeException.apply(type));
            } else if (customSettings != null && !providers.get(type).getSettingsType().isInstance(customSettings)) {
               return CompletableFuture.failedFuture(InvalidCustomSettingsException.apply(
                  type, customSettings.getClass(), providers.get(type).getSettingsType()));
            } else {
               return CompletableFuture.completedFuture(Done.getInstance());
            }
         })
         .thenCompose(checked -> {
            var state = DataAssetState.APPROVED;

            if (metadata.getZone().equals(DataZone.PREPARED)) {
               state = DataAssetState.REVIEW_REQUIRED;
            }

            var created = ActionMetadata.apply(executor);
            var properties = DataAssetProperties.apply(UID.apply(), type, metadata, state, created, created);

            return repository
               .insertOrUpdateEntity(properties)
               .thenApply(d -> getById(properties.getId()))
               .thenCompose(entity -> entity.getMembers().addMember(executor, owner, DataAssetMemberRole.OWNER).thenApply(d -> entity))
               .thenCompose(entity -> entity.getMembers().addMember(executor, steward, DataAssetMemberRole.STEWARD).thenApply(d -> entity))
               .thenCompose(entity -> {
                  if (customSettings != null) {
                     var insertPropertiesCS = entity.updateCustomProperties(providers.get(type).getDefaultProperties());
                     var insertSettingsCS = entity.updateCustomSettings(executor, customSettings);
                     return Operators.compose(
                        insertPropertiesCS, insertSettingsCS,
                        (insertProperties, insertSettings) -> entity);
                  } else {
                     return CompletableFuture.completedFuture(entity);
                  }
               })
               .thenCompose(entity -> {
                  var provider = providers.get(type);
                  return provider.onCreated(entity, customSettings);
               })
               .thenApply(d -> properties);
         });
   }

   public CompletionStage<List<DataAccessRequestProperties>> findAccessRequestsByProject(UID project) {
      return repository.findDataAccessRequestsByProject(project);
   }

   public DataAssetEntity getById(UID id) {
      return DataAssetEntity.apply(id, repository, providers);
   }

   public CompletionStage<Optional<DataAssetEntity>> findByName(String name) {
      return repository
         .findEntityByName(name)
         .thenApply(optProperties -> optProperties.map(properties -> DataAssetEntity.apply(
            properties.getId(), repository, providers)));
   }

   public CompletionStage<Optional<DataAssetEntity>> findByName(String name, String expectedType) {
      return repository
         .findEntityByNameAndType(name, expectedType)
         .thenApply(optProperties -> optProperties.map(properties -> DataAssetEntity.apply(
            properties.getId(), repository, providers)));
   }

   public CompletionStage<DataAssetEntity> getByName(String name) {
      return repository
         .getEntityByName(name)
         .thenApply(properties -> DataAssetEntity.apply(properties.getId(), repository, providers));
   }

   public CompletionStage<DataAssetEntity> getByName(String name, String expectedType) {
      return repository
         .getEntityByNameAndType(name, expectedType)
         .thenApply(properties -> DataAssetEntity.apply(properties.getId(), repository, providers));
   }

   public CompletionStage<List<DataAssetProperties>> list() {
      return repository.listEntities();
   }


   public CompletionStage<Done> removeById(UID id) {
      return repository.removeEntityById(id);
   }

   public CompletionStage<Done> removeByName(String name) {
      return repository.removeEntityByName(name);
   }

}
