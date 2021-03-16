package maquette.core.entities.data.assets_v2.ports;

import akka.Done;
import maquette.core.entities.data.assets_v2.exceptions.DataAssetNotFoundException;
import maquette.core.entities.data.assets_v2.model.DataAssetProperties;
import maquette.core.ports.common.HasMembers;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.data.DataAssetMemberRole;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface DataAssetsRepository extends HasMembers<DataAssetMemberRole> {

   /*
    * Manage asset itself
    */
   CompletionStage<Optional<DataAssetProperties>> findEntityByName(String name);

   CompletionStage<Optional<DataAssetProperties>> findEntitiesById(UID id);

   <T> CompletionStage<Optional<T>> fetchCustomProperties(UID id, Class<T> expectedType);

   default CompletionStage<DataAssetProperties> getEntitiesByName(String name) {
      return findEntityByName(name).thenCompose(opt -> opt
         .<CompletionStage<DataAssetProperties>>map(CompletableFuture::completedFuture)
         .orElseGet(() -> CompletableFuture.failedFuture(DataAssetNotFoundException.applyFromName(name))));
   }

   default CompletionStage<DataAssetProperties> getEntityById(UID id) {
      return findEntitiesById(id).thenCompose(opt -> opt
         .<CompletionStage<DataAssetProperties>>map(CompletableFuture::completedFuture)
         .orElseGet(() -> CompletableFuture.failedFuture(DataAssetNotFoundException.applyFromId(id))));
   }

   CompletionStage<Done> insertOrUpdateEntity(DataAssetProperties updated);

   CompletionStage<Done> insertOrUpdateCustomProperties(UID id, Object customProperties);

   CompletionStage<List<DataAssetProperties>> listEntities();

   CompletionStage<Done> removeEntityById(UID id);

   default CompletionStage<Done> removeEntityByName(String name) {
      return findEntityByName(name).thenCompose(opt -> {
         if (opt.isPresent()) {
            return removeEntityById(opt.get().getId());
         } else {
            return CompletableFuture.completedFuture(Done.getInstance());
         }
      });
   }

   /*
    * Manage Access Requests
    */
   CompletionStage<Optional<DataAccessRequestProperties>> findDataAccessRequestById(UID asset, UID request);

   CompletionStage<Done> insertOrUpdateDataAccessRequest(DataAccessRequestProperties request);

   CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByProject(UID project);

   CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByAsset(UID asset);

   default CompletionStage<Integer> getDataAccessRequestsCountByParent(UID asset) {
      return findDataAccessRequestsByAsset(asset).thenApply(List::size);
   }

   CompletionStage<Done> removeDataAccessRequest(UID asset, UID id);

}
