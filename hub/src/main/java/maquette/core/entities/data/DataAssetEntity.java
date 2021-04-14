package maquette.core.entities.data;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.data.exceptions.InvalidCustomPropertiesException;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.entities.data.ports.DataAssetsRepository;
import maquette.core.entities.companions.MembersCompanion;
import maquette.core.entities.data.exceptions.InvalidCustomSettingsException;
import maquette.core.entities.data.model.DataAssetMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.*;
import maquette.core.values.user.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataAssetEntity {

   private final UID id;

   private final DataAssetsRepository repository;

   private final Map<String, DataAssetProvider> providers;

   public AccessRequests getAccessRequests() {
      return AccessRequests.apply(id, repository);
   }

   public UID getId() {
      return this.id;
   }

   public <T> CompletionStage<Optional<T>> fetchCustomSettings(Class<T> expectedType) {
      return repository.fetchCustomSettings(id, expectedType);
   }

   public <T> CompletionStage<T> getCustomSettings(Class<T> expectedType) {
      return repository.fetchCustomSettings(id, expectedType).thenApply(Optional::orElseThrow);
   }

   public <T> CompletionStage<Optional<T>> fetchCustomProperties(Class<T> expectedType) {
      return repository.fetchCustomProperties(id, expectedType);
   }

   public <T> CompletionStage<T> getCustomProperties(Class<T> expectedType) {
      return repository.fetchCustomProperties(id, expectedType).thenApply(Optional::orElseThrow);
   }

   public CompletionStage<UID> getResourceId() {
      return repository
         .getEntityById(id)
         .thenApply(properties -> id.withParent(properties.getType()));
   }

   public MembersCompanion<DataAssetMemberRole> getMembers() {
      return MembersCompanion.apply(id, repository);
   }

   public CompletionStage<DataAssetProperties> getProperties() {
      return repository.getEntityById(id);
   }

   public CompletionStage<Done> approve(User executor) {
      return repository
         .getEntityById(id)
         .thenCompose(properties -> {
            var updated = properties;

            if (properties.getState().equals(DataAssetState.REVIEW_REQUIRED)) {
               updated = updated
                  .withState(DataAssetState.APPROVED)
                  .withUpdated(executor);
            }

            return repository.insertOrUpdateEntity(updated);
         });
   }

   public CompletionStage<Done> deprecate(User executor, boolean deprecate) {
      return repository
         .getEntityById(id)
         .thenCompose(properties -> {
            var updated = properties;

            if (deprecate && properties.getState().equals(DataAssetState.APPROVED)) {
               updated = updated
                  .withState(DataAssetState.DEPRECATED)
                  .withUpdated(executor);
            } else if (!deprecate && properties.getState().equals(DataAssetState.DEPRECATED)) {
               updated = updated
                  .withState(DataAssetState.APPROVED)
                  .withUpdated(executor);
            }

            return repository.insertOrUpdateEntity(updated);
         });
   }

   public CompletionStage<Done> update(User executor, DataAssetMetadata newMetadata) {

      return repository
         .getEntityById(id)
         .thenCompose(properties -> {
            var state = properties.getState();
            var meta = properties.getMetadata();
            boolean reviewRequired = false;

            if (!meta.getPersonalInformation().equals(newMetadata.getPersonalInformation())) {
               switch (meta.getPersonalInformation()) {
                  case PERSONAL_INFORMATION:
                  case SENSITIVE_PERSONAL_INFORMATION:
                     reviewRequired = true;
                     break;
                  default:
                     // ok
               }

               switch (newMetadata.getPersonalInformation()) {
                  case PERSONAL_INFORMATION:
                  case SENSITIVE_PERSONAL_INFORMATION:
                     reviewRequired = true;
                     break;
                  default:
                     // ok
               }
            }

            if (!meta.getZone().equals(newMetadata.getZone()) && newMetadata.getZone() == DataZone.GOLD) {
               reviewRequired = true;
            }

            if (state.equals(DataAssetState.APPROVED) && reviewRequired) {
               state = DataAssetState.REVIEW_REQUIRED;
            }

            var updated = properties
               .withMetadata(newMetadata)
               .withState(state)
               .withUpdated(executor);

            return repository.insertOrUpdateEntity(updated);
         });
   }

   public CompletionStage<Done> updateCustomSettings(User executor, Object customSettings) {
      return repository
         .getEntityById(id)
         .thenCompose(properties -> {
            if (!providers.get(properties.getType()).getSettingsType().isInstance(customSettings)) {
               return CompletableFuture.failedFuture(InvalidCustomSettingsException.apply(
                  properties.getType(), customSettings.getClass(), providers.get(properties.getType()).getSettingsType()));
            } else {
               return CompletableFuture.completedFuture(Done.getInstance());
            }
         })
         .thenCompose(done -> repository.insertOrUpdateCustomSettings(id, customSettings))
         .thenCompose(done -> repository.getEntityById(id))
         .thenApply(entity -> entity.withUpdated(executor))
         .thenCompose(properties -> repository.insertOrUpdateEntity(properties).thenApply(d -> properties))
         .thenCompose(properties -> providers.get(properties.getType()).onUpdatedCustomSettings(this));
   }

   public CompletionStage<Done> updateCustomProperties(Object customProperties) {
      return repository
         .getEntityById(id)
         .thenCompose(properties -> {
            if (!providers.get(properties.getType()).getPropertiesType().isInstance(customProperties)) {
               return CompletableFuture.failedFuture(InvalidCustomPropertiesException.apply(
                  properties.getType(), customProperties.getClass(), providers.get(properties.getType()).getPropertiesType()));
            } else {
               return CompletableFuture.completedFuture(Done.getInstance());
            }
         })
         .thenCompose(done -> repository.insertOrUpdateCustomProperties(id, customProperties));
   }

   public CompletionStage<Done> updated(User executor) {
      return repository
         .getEntityById(id)
         .thenCompose(properties -> repository.insertOrUpdateEntity(properties.withUpdated(executor)));
   }

}
