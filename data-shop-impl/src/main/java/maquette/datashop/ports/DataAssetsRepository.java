package maquette.datashop.ports;

import akka.Done;
import maquette.core.ports.HasMembers;
import maquette.core.values.UID;
import maquette.datashop.exceptions.DataAssetNotFoundException;
import maquette.datashop.values.DataAssetProperties;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.access_requests.DataAccessRequestProperties;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * Interface for a repository which is responsible to store data asset metadata.
 */
public interface DataAssetsRepository extends HasMembers<DataAssetMemberRole> {

   /**
    * Get a data asset by its unique name.
    *
    * @param name The name of the data asset.
    * @return The found data asset.
    */
   CompletionStage<Optional<DataAssetProperties>> findDataAssetByName(String name);

   /**
    * Like {@link DataAssetsRepository#findDataAssetByName(String)}, but also checks the type of the asset.
    *
    * @param name The name of the data asset.
    * @param type The name of the expected data asset type.
    * @return The found data asset.
    */
   default CompletionStage<Optional<DataAssetProperties>> findDataAssetByNameAndType(String name, String type) {
      return findDataAssetByName(name)
         .thenApply(opt -> opt.flatMap(properties -> {
            if (properties.getType().equals(type)) {
               return Optional.of(properties);
            } else {
               return Optional.empty();
            }
         }));
   }

   /**
    * Find an entity by its unique id.
    *
    * @param id The id of the asset.
    * @return The found data asset.
    */
   CompletionStage<Optional<DataAssetProperties>> findDataAssetsById(UID id);

   /**
    * Retrieve custom settings of a data asset.
    *
    * @param id           The id of the asset.
    * @param expectedType The expected Java type of the custom settings.
    * @param <T>          The expected Java type of the custom settings.
    * @return The custom settings, if existing.
    */
   <T> CompletionStage<Optional<T>> fetchCustomSettings(UID id, Class<T> expectedType);

   /**
    * Retrieve custom properties of a data asset.
    *
    * @param id           The id of the asset.
    * @param expectedType The expected Java type of the custom settings.
    * @param <T>          The expected Java type of the custom settings.
    * @return The custom properties, if existing.
    */
   <T> CompletionStage<Optional<T>> fetchCustomProperties(UID id, Class<T> expectedType);

   /**
    * Like {@link DataAssetsRepository#findDataAssetByName(String)} but an exception will be thrown
    * if the asset does not exist.
    *
    * @param name The name of the data asset.
    * @return The data asset.
    * @throws DataAssetNotFoundException if data asset does not exist.
    */
   default CompletionStage<DataAssetProperties> getDataAssetByName(String name) {
      return findDataAssetByName(name).thenCompose(opt -> opt
         .<CompletionStage<DataAssetProperties>>map(CompletableFuture::completedFuture)
         .orElseGet(() -> CompletableFuture.failedFuture(DataAssetNotFoundException.applyFromName(name))));
   }

   /**
    * Like {@link DataAssetsRepository#getDataAssetByNameAndType(String, String)}  but exception will be thrown
    * if the asset does not exist,
    *
    * @param name The name of the data asset.
    * @param type The name of the expected data asset type.
    * @return The data asset.
    * @throws DataAssetNotFoundException if data asset does not exist.
    */
   default CompletionStage<DataAssetProperties> getDataAssetByNameAndType(String name, String type) {
      return findDataAssetByNameAndType(name, type).thenCompose(opt -> opt
         .map(CompletableFuture::completedFuture)
         .orElseGet(() -> CompletableFuture.failedFuture(DataAssetNotFoundException.applyFromName(name))));
   }

   /**
    * Like {@link DataAssetsRepository#getDataAssetById(UID)} but throws exception if asset does not exist.
    *
    * @param id The id of the data asset.
    * @return The data asset.
    * @throws DataAssetNotFoundException if data asset does not exist.
    */
   default CompletionStage<DataAssetProperties> getDataAssetById(UID id) {
      return findDataAssetsById(id).thenCompose(opt -> opt
         .<CompletionStage<DataAssetProperties>>map(CompletableFuture::completedFuture)
         .orElseGet(() -> CompletableFuture.failedFuture(DataAssetNotFoundException.applyFromId(id))));
   }

   /**
    * Insert or update properties of a data asset.
    *
    * @param updated The updated or new data asset.
    * @return Done.
    */
   CompletionStage<Done> insertOrUpdateDataAsset(DataAssetProperties updated);

   /**
    * Insert or update custom settings of a data asset.
    *
    * @param id             The unique id of the data asset.
    * @param customSettings The custom settings object. The settings can be serialized to JSON.
    * @return Done.
    */
   CompletionStage<Done> insertOrUpdateCustomSettings(UID id, Object customSettings);

   /**
    * Insert or update custom properties of a data asset.
    *
    * @param id               The unique ude of the data asset.
    * @param customProperties The custom properties object. The object can be serialized to JSON.
    * @return Done.
    */
   CompletionStage<Done> insertOrUpdateCustomProperties(UID id, Object customProperties);

   /**
    * List all stored entities.
    *
    * @return Done.
    */
   CompletionStage<Stream<DataAssetProperties>> listEntities();

   /**
    * Remove a data asset by it's id.
    *
    * @param id The id of the data asset.
    * @return Done.
    */
   CompletionStage<Done> removeDataAssetById(UID id);

   /**
    * Remove a data asset by it's name.
    *
    * @param name The name of the data asset.
    * @return Done.
    */
   default CompletionStage<Done> removeDataAssetByName(String name) {
      return findDataAssetByName(name).thenCompose(opt -> {
         if (opt.isPresent()) {
            return removeDataAssetById(opt.get().getId());
         } else {
            return CompletableFuture.completedFuture(Done.getInstance());
         }
      });
   }

   /**
    * Find a data asset request.
    *
    * @param asset   The id of the data asset.
    * @param request The id of the data access request.
    * @return The data access request.
    */
   CompletionStage<Optional<DataAccessRequestProperties>> findDataAccessRequestById(UID asset, UID request);

   /**
    * Insert or update a data access request.
    *
    * @param request The request to be inserted.
    * @return Done.
    */
   CompletionStage<Done> insertOrUpdateDataAccessRequest(DataAccessRequestProperties request);

   /**
    * Find and list data access requests related to a project.
    *
    * @param workspace The unique id of the workspace.
    * @return The list of access requests found.
    */
   CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByWorkspace(UID workspace);

   /**
    * Find data access requests related to a specific data asset.
    *
    * @param asset The unique id of the data asset.
    * @return The list of access requests found.
    */
   CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByAsset(UID asset);

   /**
    * Retrieve the number of data access requests (in any state) related to a data asset.
    *
    * @param asset The unique id of the data asset.
    * @return The number of access requests for this data asset.
    */
   default CompletionStage<Integer> getDataAccessRequestsCountByParent(UID asset) {
      return findDataAccessRequestsByAsset(asset).thenApply(List::size);
   }

   /**
    * Remove a data access request.
    *
    * @param asset The unique id of the data asset.
    * @param id The id of the data access request to be deleted.
    * @return Done.
    */
   CompletionStage<Done> removeDataAccessRequest(UID asset, UID id);

}
