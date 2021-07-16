package maquette.datashop.entities;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.ports.MembersCompanion;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.datashop.exceptions.InvalidCustomPropertiesException;
import maquette.datashop.exceptions.InvalidCustomSettingsException;
import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.values.providers.DataAssetProviders;
import maquette.datashop.values.access.DataAssetMemberRole;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataAssetEntity {

   private final UID id;

   private final DataAssetsRepository repository;

   private final DataAssetProviders providers;

   public MembersCompanion<DataAssetMemberRole> getMembers() {
      return MembersCompanion.apply(id, repository);
   }

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

}
