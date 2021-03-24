package maquette.asset_providers.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.asset_providers.datasets.exceptions.RevisionNotFoundException;
import maquette.asset_providers.datasets.exceptions.VersionNotFoundException;
import maquette.asset_providers.datasets.model.CommittedRevision;
import maquette.asset_providers.datasets.model.DatasetVersion;
import maquette.asset_providers.datasets.model.OpenRevision;
import maquette.asset_providers.datasets.model.Revision;
import maquette.common.Operators;
import maquette.core.entities.data.assets_v2.DataAssetEntity;
import maquette.core.ports.DataExplorer;
import maquette.core.ports.RecordsStore;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.User;
import org.apache.avro.Schema;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DatasetEntity {

   private final DatasetsRepository repository;

   private final RecordsStore store;

   private final DataExplorer explorer;

   private final DataAssetEntity entity;

   public CompletionStage<Done> analyze(DatasetVersion version) {
      var revisionCS = repository
         .findRevisionByVersion(entity.getId(), version)
         .thenApply(opt -> opt.orElseThrow(() -> VersionNotFoundException.apply(version)));

      var datasetCS = entity.getProperties();

      return Operators
         .compose(
            revisionCS, datasetCS,
            (revision, dataset) -> explorer
               .analyze(dataset.getMetadata().getName(), revision.getVersion().toString())
               .thenApply(revision::withStatistics)
               .thenCompose(r -> repository.insertOrUpdateRevision(entity.getId(), r)))
         .thenCompose(done -> done);
   }

   public CompletionStage<CommittedRevision> commit(User executor, UID revisionId, String message) {
      return repository
         .findRevisionById(entity.getId(), revisionId)
         .thenCompose(maybeRevision -> {
            if (maybeRevision.isPresent()) {
               var revision = maybeRevision.get();

               return getNextVersion((revision.getSchema()))
                  .thenCompose(version -> {
                     var revisionUpdated = CommittedRevision.apply(
                        revision.getId(), revision.getCreated(), ActionMetadata.apply(executor), ActionMetadata.apply(executor),
                        revision.getRecords(), revision.getSchema(), version, message);

                     return repository
                        .insertOrUpdateRevision(entity.getId(), revisionUpdated)
                        .thenApply(d -> revisionUpdated);
                  });
            } else {
               throw RevisionNotFoundException.apply(revisionId);
            }
         });
   }

   public CompletionStage<Revision> createRevision(User executor, Schema schema) {
      var revision = OpenRevision.apply(UID.apply(), ActionMetadata.apply(executor), ActionMetadata.apply(executor), 0, schema);
      return repository
         .insertOrUpdateRevision(entity.getId(), revision)
         .thenApply(d -> revision);
   }

   public CompletionStage<Records> download(User executor, DatasetVersion version) {
      return repository
         .findRevisionByVersion(entity.getId(), version)
         .thenCompose(maybeRevision -> {
            if (maybeRevision.isPresent()) {
               var revision = maybeRevision.get();
               return store.get(getRevisionKey(revision.getId()));
            } else {
               throw VersionNotFoundException.apply(version.toString());
            }
         });
   }

   public CompletionStage<Records> download(User executor) {
      return getVersions()
         .thenApply(versions -> versions.stream().map(CommittedRevision::getVersion).findFirst().orElse(DatasetVersion.apply("1.0.0")))
         .thenCompose(version -> download(executor, version));
   }

   public CompletionStage<List<CommittedRevision>> getVersions() {
      return repository
         .findAllRevisions(entity.getId())
         .thenApply(revisions -> revisions
            .stream()
            .filter(r -> r instanceof CommittedRevision)
            .map(r -> (CommittedRevision) r)
            .sorted(Comparator.comparing(CommittedRevision::getVersion).reversed())
            .collect(Collectors.toList()));
   }

   public CompletionStage<Done> upload(User executor, UID revisionId, Records records) {
      return repository
         .findRevisionById(entity.getId(), revisionId)
         .thenCompose(maybeRevision -> {
            if (maybeRevision.isPresent()) {
               var revision = maybeRevision.get();

               return store
                  .append(getRevisionKey(revision.getId()), records)
                  .thenCompose(d -> {
                     var revisionUpdated = revision
                        .withRecords(revision.getRecords() + records.size())
                        .withModified(ActionMetadata.apply(executor));

                     return repository
                        .insertOrUpdateRevision(entity.getId(), revisionUpdated);
                  });
            } else {
               throw RevisionNotFoundException.apply(revisionId);
            }
         });
   }

   private CompletionStage<DatasetVersion> getNextVersion(Schema schema) {
      return getVersions()
         .thenApply(versions -> {
            if (versions.isEmpty()) {
               return DatasetVersion.apply(1, 0, 0);
            } else {
               var compatible = schema
                  .getFields()
                  .stream()
                  .noneMatch(field -> {
                     Schema.Field existingField = versions.get(0).getSchema().getField(field.name());
                     return !field.equals(existingField);
                  });

               var version = versions.get(0).getVersion();

               if (compatible) {
                  return DatasetVersion.apply(version.getMajor(), version.getMinor() + 1, 0);
               } else {
                  return DatasetVersion.apply(version.getMajor() + 1, 0, 0);
               }
            }
         });
   }

   private String getRevisionKey(UID revisionId) {
      return String.format("%s_%s", entity.getId().getValue(), revisionId.getValue());
   }

}
