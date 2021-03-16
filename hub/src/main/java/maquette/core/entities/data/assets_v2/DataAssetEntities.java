package maquette.core.entities.data.assets_v2;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.data.assets_v2.exceptions.DataAssetAlreadyExistsException;
import maquette.core.entities.data.assets_v2.exceptions.InvalidCustomPropertiesException;
import maquette.core.entities.data.assets_v2.exceptions.UnknownDataAssetTypeException;
import maquette.core.entities.data.assets_v2.model.DataAssetMetadata;
import maquette.core.entities.data.assets_v2.model.DataAssetProperties;
import maquette.core.entities.data.assets_v2.ports.DataAssetsRepository;
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
      User executor, String type, DataAssetMetadata metadata, Authorization owner, Authorization steward, @Nullable Object customProperties) {

      return repository
         .findEntityByName(metadata.getName())
         .thenCompose(optEntity -> {
            if (optEntity.isPresent()) {
               return CompletableFuture.failedFuture(DataAssetAlreadyExistsException.withName(metadata.getName()));
            } else if (!providers.containsKey(type)) {
               return CompletableFuture.failedFuture(UnknownDataAssetTypeException.apply(type));
            } else if (customProperties != null && !providers.get(type).getPropertiesType().isInstance(customProperties)) {
               return CompletableFuture.failedFuture(InvalidCustomPropertiesException.apply(
                  type, customProperties.getClass(), providers.get(type).getPropertiesType()));
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
                  if (customProperties != null) {
                     return entity.updateCustomProperties(executor, customProperties);
                  } else {
                     return CompletableFuture.completedFuture(Done.getInstance());
                  }
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

   public CompletionStage<DataAssetEntity> getByName(String name) {
      return repository
         .getEntitiesByName(name)
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
