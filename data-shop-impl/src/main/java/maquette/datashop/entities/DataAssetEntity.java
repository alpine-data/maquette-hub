package maquette.datashop.entities;

import akka.Done;
import akka.japi.Function;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.ports.MembersCompanion;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.datashop.exceptions.InvalidCustomPropertiesException;
import maquette.datashop.exceptions.InvalidCustomSettingsException;
import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.values.DataAssetProperties;
import maquette.datashop.values.DataAssetState;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.metadata.DataAssetMetadata;
import maquette.datashop.values.providers.DataAssetProviders;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataAssetEntity {

   private final UID id;

   private final DataAssetsRepository repository;

   private final DataAssetProviders providers;

   /**
    * Update data asset state to be approved. This means that the asset has been reviewed by the responsible
    * data owner.
    *
    * @param executor The user who executes the action.
    * @return Done.
    */
   public CompletionStage<Done> approve(User executor) {
      return repository
         .getDataAssetById(id)
         .thenCompose(properties -> {
            var updated = properties;

            if (properties.getState().equals(DataAssetState.REVIEW_REQUIRED)) {
               updated = updated
                  .withState(DataAssetState.APPROVED)
                  .withUpdated(executor);
            }

            return repository.insertOrUpdateDataAsset(updated);
         });
   }

   /**
    * Update the data assets state to be declined (usually if current state is review required). This is usually
    * done by the data owner, when there are issues with the current configuration or documentation of the
    * data asset.
    *
    * @param executor The user who executes the action.
    * @return Done.
    */
   public CompletionStage<Done> decline(User executor) {
      return repository
         .getDataAssetById(id)
         .thenApply(properties -> properties
            .withState(DataAssetState.DECLINED)
            .withUpdated(executor))
         .thenCompose(repository::insertOrUpdateDataAsset);
   }

   /**
    * Update the data assets state to be deprecated. This shows that the data asset shouldn't be used any more.
    * New subscriptions to the asset are not allowed anymore. Already connected subscriptions will continue to work.
    *
    * @param executor  The user who executes the action.
    * @param deprecate If true, the asset will be deprecated. If false, deprecation will be reverted.
    * @return Done.
    */
   public CompletionStage<Done> deprecate(User executor, boolean deprecate) {
      // TODO mw: The implementation is not correct yet. The state needs to be added to the state model.
      return repository
         .getDataAssetById(id)
         .thenCompose(properties -> {
            var updated = properties;

            if (deprecate && properties.getState().equals(DataAssetState.APPROVED)) {
               updated = updated
                  .withState(DataAssetState.DECLINED)
                  .withUpdated(executor);
            } else if (!deprecate && properties.getState().equals(DataAssetState.DECLINED)) {
               updated = updated
                  .withState(DataAssetState.APPROVED)
                  .withUpdated(executor);
            }

            return repository.insertOrUpdateDataAsset(updated);
         });
   }

   /**
    * Read custom settings of the data asset.
    *
    * @param expectedType The expected type of the settings.
    * @param <T>          The expected type.
    * @return The settings, if available.
    */
   public <T> CompletionStage<Optional<T>> fetchCustomSettings(Class<T> expectedType) {
      return repository.fetchCustomSettings(id, expectedType);
   }

   /**
    * Like fetchCustomSettings, except that existence of settings is expected. An exception si thrown
    * if settings are missing.
    *
    * @param expectedType The expected type of the settings.
    * @param <T>          The expected type.
    * @return The settings.
    */
   public <T> CompletionStage<T> getCustomSettings(Class<T> expectedType) {
      return repository.fetchCustomSettings(id, expectedType).thenApply(Optional::orElseThrow);
   }

   /**
    * Read custom properties of the data asset.
    *
    * @param expectedType The expected type of the properties.
    * @param <T>          The expected type.
    * @return The properties, if present.
    */
   public <T> CompletionStage<Optional<T>> fetchCustomProperties(Class<T> expectedType) {
      return repository.fetchCustomProperties(id, expectedType);
   }

   /**
    * Like fetchCustomProperties, but expects that properties exist. If properties do not exist,
    * an exception will be thrown.
    *
    * @param expectedType The extecped type of the properties.
    * @param <T>          The expected type.
    * @return The properties.
    */
   public <T> CompletionStage<T> getCustomProperties(Class<T> expectedType) {
      return repository.fetchCustomProperties(id, expectedType).thenApply(Optional::orElseThrow);
   }

   /**
    * Returns a companion object to work with members of the data asset.
    *
    * @return The companion.
    */
   public MembersCompanion<DataAssetMemberRole> getMembers() {
      return MembersCompanion.apply(id, repository);
   }

   /**
    * Just return data asset properties.
    *
    * @return The properties.
    */
   public CompletionStage<DataAssetProperties> getProperties() {
      return repository.getDataAssetById(id);
   }

   /**
    * Returns a companion object to work with access requests.
    *
    * @return The companion.
    */
   public AccessRequestsCompanion getAccessRequests() {
      return AccessRequestsCompanion.apply(id, repository);
   }

   /**
    * Pro-actively ask for review of the Data Owner.
    *
    * @param executor The user who executes the action.
    * @return Done
    */
   public CompletionStage<Done> requestReview(User executor) {
      return repository
         .getDataAssetById(id)
         .thenApply(properties -> properties
            .withState(DataAssetState.REVIEW_REQUIRED)
            .withUpdated(executor))
         .thenCompose(repository::insertOrUpdateDataAsset);
   }

   /**
    * Updates the metadata of a data asset. Based on the changed properties, the state of the asset
    * might also change to require a new review by the data owner.
    *
    * @param executor    The user who executes the action.
    * @param newMetadata The updated metadata.
    * @return Done.
    */
   public CompletionStage<Done> update(User executor, DataAssetMetadata newMetadata) {
      var isOwnerCS = getMembers()
         .getMembers()
         .thenApply(members -> members
            .stream()
            .anyMatch(granted -> granted.getAuthorization().authorizes(executor) && granted.getRole().equals(DataAssetMemberRole.OWNER)));

      var propertiesCS = repository.getDataAssetById(id);

      return Operators.compose(isOwnerCS, propertiesCS, (isOwner, properties) -> {
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

         if (!meta.getZone().equals(newMetadata.getZone())) {
            switch (newMetadata.getZone()) {
               case PREPARED:
               case GOLD:
                  reviewRequired = true;
               default:
                  // ok
            }
         }

         if (reviewRequired && !isOwner) {
            state = DataAssetState.REVIEW_REQUIRED;
         }

         var updated = properties
            .withMetadata(newMetadata)
            .withState(state)
            .withUpdated(executor);

         return repository.insertOrUpdateDataAsset(updated);
      }).thenCompose(cs -> cs);
   }

   /**
    * Updates the custom settings of the data asset.
    * <p>
    * Custom settings are configured by end-users, like data asset metadata. A data asset type should have defined
    * a type for its custom settings.
    *
    * @param executor       The user who executes the action.
    * @param customSettings The new settings.
    * @return Done.
    */
   public CompletionStage<Done> updateCustomSettings(User executor, Object customSettings) {
      return repository
         .getDataAssetById(id)
         .thenCompose(properties -> {
            if (!providers.getByName(properties.getType()).getSettingsType().isInstance(customSettings)) {
               return CompletableFuture.failedFuture(InvalidCustomSettingsException.apply(
                  properties.getType(), customSettings.getClass(), providers.getByName(properties.getType()).getSettingsType()));
            } else {
               return CompletableFuture.completedFuture(Done.getInstance());
            }
         })
         .thenCompose(done -> repository.insertOrUpdateCustomSettings(id, customSettings))
         .thenCompose(done -> repository.getDataAssetById(id))
         .thenApply(entity -> entity.withUpdated(executor))
         .thenCompose(properties -> repository.insertOrUpdateDataAsset(properties).thenApply(d -> properties))
         .thenCompose(properties -> providers.getByName(properties.getType()).onUpdatedCustomSettings(this));
   }

   /**
    * Updates custom properties of a data asset.
    * <p>
    * Properties are state information of a data asset which are usually managed by the backend aka the actual
    * data asset provider.
    * <p>
    * Each data asset type/ provider specifies its own properties type.
    *
    * @param customProperties The updated properties.
    * @return Done.
    */
   public CompletionStage<Done> updateCustomProperties(Object customProperties) {
      return repository
         .getDataAssetById(id)
         .thenCompose(properties -> {
            if (!providers.getByName(properties.getType()).getPropertiesType().isInstance(customProperties)) {
               return CompletableFuture.failedFuture(InvalidCustomPropertiesException.apply(
                  properties.getType(), customProperties.getClass(), providers.getByName(properties.getType()).getPropertiesType()));
            } else {
               return CompletableFuture.completedFuture(Done.getInstance());
            }
         })
         .thenCompose(done -> repository.insertOrUpdateCustomProperties(id, customProperties));
   }

   /**
    * Convenience function to read properties and store updated properties.
    *
    * @param expectedType The property type.
    * @param updater      A function which does the actual update logic.
    * @param <T>          The property type.
    * @return Done.
    */
   public <T> CompletionStage<Done> readAndUpdateCustomProperties(Class<T> expectedType, Function<T, T> updater) {
      return this.getCustomProperties(expectedType)
         .thenApply(properties -> Operators.suppressExceptions(() -> updater.apply(properties)))
         .thenCompose(this::updateCustomProperties);
   }

   /**
    * Convenience function to update the last modified property of the data asset.
    *
    * @param executor The user who executed the last action.
    * @return Done.
    */
   public CompletionStage<Done> updated(User executor) {
      return repository
         .getDataAssetById(id)
         .thenCompose(properties -> repository.insertOrUpdateDataAsset(properties.withUpdated(executor)));
   }

}
