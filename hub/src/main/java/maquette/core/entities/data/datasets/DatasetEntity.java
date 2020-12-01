package maquette.core.entities.data.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.entities.companions.MembersCompanion;
import maquette.core.entities.data.datasets.exceptions.DatasetNotFoundException;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.ports.DatasetsRepository;
import maquette.core.ports.DatasetsStore;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class DatasetEntity {

   private final UID id;

   private final DatasetsRepository repository;

   private final DatasetsStore store;

   public AccessRequests accessRequests() {
      return AccessRequests.apply(id, repository);
   }

   public Revisions revisions() {
      return Revisions.apply(id, repository, store);
   }

   public MembersCompanion<DataAssetMemberRole> members() { return MembersCompanion.apply(id, repository); }

   public CompletionStage<DatasetProperties> getProperties() {
      return withProperties(CompletableFuture::completedFuture);
   }

   public CompletionStage<Done> updateProperties(
      User executor, String name, String title, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {

      // TODO mw: value validation ...

      return withProperties(properties -> {
         var updated = properties
            .withName(name)
            .withTitle(title)
            .withSummary(summary)
            .withVisibility(visibility)
            .withClassification(classification)
            .withPersonalInformation(personalInformation)
            .withUpdated(ActionMetadata.apply(executor));

         return repository.insertOrUpdateDataset(updated);
      });
   }

   private <T> CompletionStage<T> withProperties(Function<DatasetProperties, CompletionStage<T>> func) {
      return repository
         .findDatasetById(id)
         .thenApply(opt -> opt.orElseThrow(() -> DatasetNotFoundException.withId(id)))
         .thenCompose(func);
   }

}
