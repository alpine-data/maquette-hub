package maquette.core.entities.data.collections;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.companions.AccessLogsCompanion;
import maquette.core.entities.companions.MembersCompanion;
import maquette.core.entities.data.DataAssetEntity;
import maquette.core.entities.data.collections.model.CollectionProperties;
import maquette.core.entities.data.datasets.AccessRequests;
import maquette.core.entities.data.datasources.exceptions.DataSourceNotFoundException;
import maquette.core.ports.CollectionsRepository;
import maquette.core.ports.ObjectStore;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.*;
import maquette.core.values.user.User;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@AllArgsConstructor(staticName = "apply")
public final class CollectionEntity implements DataAssetEntity<CollectionProperties> {

   private final UID id;

   private final CollectionsRepository repository;

   @Override
   public AccessLogsCompanion getAccessLogs() {
      return AccessLogsCompanion.apply(id, repository);
   }

   @Override
   public AccessRequests<CollectionProperties> getAccessRequests() {
      return AccessRequests.apply(id, repository, this::getProperties);
   }

   public CollectionFiles getFiles() {
      return CollectionFiles.apply(id, repository);
   }

   @Override
   public MembersCompanion<DataAssetMemberRole> getMembers() {
      return MembersCompanion.apply(id, repository);
   }

   @Override
   public UID getId() {
      return id;
   }

   @Override
   public CompletionStage<CollectionProperties> getProperties() {
      return withProperties(CompletableFuture::completedFuture);
   }

   public CompletionStage<Done> update(
      User executor, String name, String title, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation, DataZone zone) {

      // TODO mw: value validation ...

      return withProperties(properties -> {
         var updated = properties
            .withName(name)
            .withTitle(title)
            .withSummary(summary)
            .withVisibility(visibility)
            .withClassification(classification)
            .withPersonalInformation(personalInformation)
            .withUpdated(ActionMetadata.apply(executor))
            .withZone(zone);

         return repository.insertOrUpdateAsset(updated);
      });
   }

   private <T> CompletionStage<T> withProperties(Function<CollectionProperties, CompletionStage<T>> func) {
      return repository
         .findAssetById(id)
         .thenApply(opt -> opt.orElseThrow(() -> DataSourceNotFoundException.withId(id)))
         .thenCompose(func);
   }

}
