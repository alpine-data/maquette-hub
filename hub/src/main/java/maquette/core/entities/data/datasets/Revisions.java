package maquette.core.entities.data.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.exceptions.DatasetNotFoundException;
import maquette.core.entities.data.datasets.exceptions.RevisionNotFoundException;
import maquette.core.entities.data.datasets.exceptions.VersionNotFoundException;
import maquette.core.entities.data.datasets.model.DatasetVersion;
import maquette.core.entities.data.datasets.model.records.Records;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.data.datasets.model.revisions.OpenRevision;
import maquette.core.entities.data.datasets.model.revisions.Revision;
import maquette.core.ports.DataExplorer;
import maquette.core.ports.DatasetsRepository;
import maquette.core.ports.DatasetsStore;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import org.apache.avro.Schema;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class Revisions {

   private final UID id;

   private final DatasetsRepository repository;

   private final DatasetsStore store;

   private final DataExplorer explorer;

   public CompletionStage<Done> analyze(DatasetVersion version) {
      var revisionCS = repository
         .findRevisionByVersion(id, version)
         .thenApply(opt -> opt.orElseThrow(() -> VersionNotFoundException.apply(version)));

      var datasetCS = repository
         .findAssetById(id)
         .thenApply(opt -> opt.orElseThrow(() -> DatasetNotFoundException.withId(id)));

      return Operators
         .compose(
            revisionCS, datasetCS,
            (revision, dataset) -> explorer
               .analyze(dataset.getName(), revision.getVersion())
               .thenApply(revision::withStatistics)
               .thenCompose(r -> repository.insertOrUpdateRevision(id, r)))
         .thenCompose(done -> done);
   }

   public CompletionStage<CommittedRevision> commit(User executor, UID revisionId, String message) {
      return repository
         .findRevisionById(id, revisionId)
         .thenCompose(maybeRevision -> {
            if (maybeRevision.isPresent()) {
               var revision = maybeRevision.get();

               return getNextVersion((revision.getSchema()))
                  .thenCompose(version -> {
                     var revisionUpdated = CommittedRevision.apply(
                        revision.getId(), revision.getCreated(), ActionMetadata.apply(executor), ActionMetadata.apply(executor),
                        revision.getRecords(), revision.getSchema(), version, message);

                     return repository
                        .insertOrUpdateRevision(id, revisionUpdated)
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
         .insertOrUpdateRevision(id, revision)
         .thenApply(d -> revision);
   }

   public CompletionStage<Records> download(User executor, DatasetVersion version) {
      return repository
         .findRevisionByVersion(id, version)
         .thenCompose(maybeRevision -> {
            if (maybeRevision.isPresent()) {
               var revision = maybeRevision.get();
               return store.get(getRevisionKey(revision.getId()));
            } else {
               throw VersionNotFoundException.apply(version.toString());
            }
         });
   }

   public CompletionStage<List<CommittedRevision>> getVersions() {
      return repository
         .findAllRevisions(id)
         .thenApply(revisions -> revisions
            .stream()
            .filter(r -> r instanceof CommittedRevision)
            .map(r -> (CommittedRevision) r)
            .sorted(Comparator.comparing(CommittedRevision::getVersion).reversed())
            .collect(Collectors.toList()));
   }

   public CompletionStage<Done> upload(User executor, UID revisionId, Records records) {
      return repository
         .findRevisionById(id, revisionId)
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
                        .insertOrUpdateRevision(id, revisionUpdated);
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
      return String.format("%s_%s", id.getValue(), revisionId.getValue());
   }

}
