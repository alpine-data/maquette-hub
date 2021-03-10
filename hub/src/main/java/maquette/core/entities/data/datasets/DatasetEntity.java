package maquette.core.entities.data.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.entities.companions.AccessLogsCompanion;
import maquette.core.entities.data.assets.AccessRequests;
import maquette.core.entities.companions.MembersCompanion;
import maquette.core.entities.data.assets.DataAssetEntity;
import maquette.core.entities.data.datasets.exceptions.DatasetNotFoundException;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.ports.DataExplorer;
import maquette.core.ports.DatasetsRepository;
import maquette.core.ports.RecordsStore;
import maquette.core.values.UID;
import maquette.core.values.data.*;
import maquette.core.values.user.User;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class DatasetEntity implements DataAssetEntity<DatasetProperties> {

   private final UID id;

   private final DatasetsRepository repository;

   private final RecordsStore store;

   private final DataExplorer dataExplorer;

   public AccessRequests<DatasetProperties> getAccessRequests() {
      return AccessRequests.apply(id, repository, this::getProperties);
   }

   public AccessLogsCompanion getAccessLogs() {
      return AccessLogsCompanion.apply(id, repository);
   }

   public Revisions getRevisions() {
      return Revisions.apply(id, repository, store, dataExplorer);
   }

   public MembersCompanion<DataAssetMemberRole> getMembers() {
      return MembersCompanion.apply(id, repository);
   }

   public CompletionStage<DatasetProperties> getProperties() {
      return withProperties(CompletableFuture::completedFuture);
   }

   public CompletionStage<Done> approve(User executor) {
      return withProperties(properties -> {
         var updated = properties;

         if (properties.getState().equals(DataAssetState.REVIEW_REQUIRED)) {
            updated = updated
               .withState(DataAssetState.APPROVED)
               .withUpdated(executor);
         }

         return repository.insertOrUpdateAsset(updated);
      });
   }

   public CompletionStage<Done> deprecate(User executor, boolean deprecate) {
      return withProperties(properties -> {
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

         return repository.insertOrUpdateAsset(updated);
      });
   }

   public CompletionStage<Done> update(
      User executor, String name, String title, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation, DataZone zone) {


      return withProperties(properties -> {
         var state = properties.getState();
         boolean reviewRequired = false;

         if (!properties.getPersonalInformation().equals(personalInformation)) {
            switch (properties.getPersonalInformation()) {
               case PERSONAL_INFORMATION:
               case SENSITIVE_PERSONAL_INFORMATION:
                  reviewRequired = true;
                  break;
               default:
                  // ok
            }

            switch (personalInformation) {
               case PERSONAL_INFORMATION:
               case SENSITIVE_PERSONAL_INFORMATION:
                  reviewRequired = true;
                  break;
               default:
                  // ok
            }
         }

         if (!properties.getZone().equals(zone) && zone == DataZone.GOLD) {
            reviewRequired = true;
         }

         if (state.equals(DataAssetState.APPROVED) && reviewRequired) {
            state = DataAssetState.REVIEW_REQUIRED;
         }

         var updated = properties
            .withName(name)
            .withTitle(title)
            .withSummary(summary)
            .withVisibility(visibility)
            .withClassification(classification)
            .withPersonalInformation(personalInformation)
            .withZone(zone)
            .withState(state)
            .withUpdated(executor);

         return repository.insertOrUpdateAsset(updated);
      });
   }

   private <T> CompletionStage<T> withProperties(Function<DatasetProperties, CompletionStage<T>> func) {
      return repository
         .findAssetById(id)
         .thenApply(opt -> opt.orElseThrow(() -> DatasetNotFoundException.withId(id)))
         .thenCompose(func);
   }

}
