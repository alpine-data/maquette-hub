package maquette.core.entities.data.collections;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.data.collections.exceptions.CollectionAlreadyExistsException;
import maquette.core.entities.data.collections.exceptions.CollectionNotFoundException;
import maquette.core.entities.data.collections.model.CollectionProperties;
import maquette.core.entities.data.collections.model.FileEntry;
import maquette.core.ports.CollectionsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.data.*;
import maquette.core.values.user.User;
import org.apache.commons.lang.NotImplementedException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class CollectionEntities implements DataAssetEntities<CollectionProperties, CollectionEntity> {

   private final CollectionsRepository repository;

   public CompletionStage<CollectionProperties> create(
      User executor, String title, String name, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation,
      DataZone zone, Authorization owner, Authorization steward) {

      return repository
         .findAssetByName(name)
         .thenCompose(maybeCollection -> {
            if (maybeCollection.isPresent()) {
               return CompletableFuture.failedFuture(CollectionAlreadyExistsException.withName(name));
            } else {
               var created = ActionMetadata.apply(executor);
               var collection = CollectionProperties.apply(
                  UID.apply(), title, name, summary, FileEntry.Directory.apply(),
                  visibility, classification, personalInformation, zone, DataAssetState.APPROVED, created, created);

               return repository
                  .insertOrUpdateAsset(collection)
                  .thenCompose(d -> getById(collection.getId()))
                  .thenCompose(c -> c.getMembers().addMember(executor, owner, DataAssetMemberRole.OWNER).thenApply(i -> c))
                  .thenCompose(c -> c.getMembers().addMember(executor, steward, DataAssetMemberRole.STEWARD))
                  .thenApply(d -> collection);
            }
         });
   }

   @Override
   public CompletionStage<List<DataAccessRequestProperties>> findAccessRequestsByProject(UID project) {
      return repository.findDataAccessRequestsByProject(project);
   }

   @Override
   public CompletionStage<Optional<CollectionEntity>> findById(UID asset) {
      return repository
         .findAssetById(asset)
         .thenApply(maybeAsset -> maybeAsset.map(properties -> CollectionEntity.apply(properties.getId(), repository)));
   }

   @Override
   public CompletionStage<Optional<CollectionEntity>> findByName(String asset) {
      return repository
         .findAssetByName(asset)
         .thenApply(maybeAsset -> maybeAsset.map(properties -> CollectionEntity.apply(properties.getId(), repository)));
   }

   @Override
   public CompletionStage<CollectionEntity> getById(UID asset) {
      return findById(asset).thenApply(opt -> opt.orElseThrow(() -> CollectionNotFoundException.withId(asset)));
   }

   @Override
   public CompletionStage<CollectionEntity> getByName(String asset) {
      return findByName(asset).thenApply(opt -> opt.orElseThrow(() -> CollectionNotFoundException.withName(asset)));
   }

   @Override
   public CompletionStage<List<CollectionProperties>> list() {
      return repository.findAllAssets();
   }

   @Override
   public CompletionStage<Done> remove(UID asset) {
      throw new NotImplementedException();
   }

}
