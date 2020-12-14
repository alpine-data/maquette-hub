package maquette.core.entities.data.streams;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.companions.MembersCompanion;
import maquette.core.entities.data.DataAssetEntity;
import maquette.core.entities.data.datasets.AccessRequests;
import maquette.core.entities.data.datasources.exceptions.DataSourceNotFoundException;
import maquette.core.entities.data.streams.model.Retention;
import maquette.core.entities.data.streams.model.StreamProperties;
import maquette.core.ports.StreamsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;
import org.apache.avro.Schema;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@AllArgsConstructor(staticName = "apply")
public class StreamEntity implements DataAssetEntity<StreamProperties> {

   private final UID id;

   private final StreamsRepository repository;

   @Override
   public AccessRequests<StreamProperties> getAccessRequests() {
      return AccessRequests.apply(id, repository, this::getProperties);
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
   public CompletionStage<StreamProperties> getProperties() {
      return withProperties(CompletableFuture::completedFuture);
   }

   public CompletionStage<Done> update(
      User executor, String name, String title, String summary, Retention retention, Schema schema,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {

      // TODO mw: value validation ...

      return withProperties(properties -> {
         var updated = properties
            .withName(name)
            .withTitle(title)
            .withSummary(summary)
            .withRetention(retention)
            .withSchema(schema)
            .withVisibility(visibility)
            .withClassification(classification)
            .withPersonalInformation(personalInformation)
            .withUpdated(ActionMetadata.apply(executor));

         return repository.insertOrUpdateAsset(updated);
      });
   }

   private <T> CompletionStage<T> withProperties(Function<StreamProperties, CompletionStage<T>> func) {
      return repository
         .findAssetById(id)
         .thenApply(opt -> opt.orElseThrow(() -> DataSourceNotFoundException.withId(id)))
         .thenCompose(func);
   }

}
