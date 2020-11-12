package maquette.core.entities.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.common.Operators;
import maquette.core.entities.datasets.exceptions.RevisionNotFoundException;
import maquette.core.entities.datasets.exceptions.VersionNotFoundException;
import maquette.core.entities.datasets.model.DatasetVersion;
import maquette.core.entities.datasets.model.records.Records;
import maquette.core.entities.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.datasets.model.revisions.OpenRevision;
import maquette.core.entities.datasets.model.revisions.Revision;
import maquette.core.ports.DatasetsDataStore;
import maquette.core.ports.DatasetsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.user.User;
import org.apache.avro.Schema;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class Revisions {

   private final String id;

   private final String projectId;

   private final String fullId;

   private final String name;

   private final DatasetsRepository repository;

   private final DatasetsDataStore store;

   public CompletionStage<CommittedRevision> commit(User executor, String revisionId, String message) {
      return repository
         .findRevisionById(projectId, id, revisionId)
         .thenCompose(maybeRevision -> {
            if (maybeRevision.isPresent()) {
               var revision = maybeRevision.get();

               return getNextVersion((revision.getSchema()))
                  .thenCompose(version -> {
                     var revisionUpdated = CommittedRevision.apply(
                        revision.getId(), revision.getCreated(), ActionMetadata.apply(executor), ActionMetadata.apply(executor),
                        revision.getRecords(), revision.getSchema(), version, message);

                     return repository
                        .insertOrUpdateRevision(projectId, id, revisionUpdated)
                        .thenApply(d -> revisionUpdated);
                  });
            } else {
               throw RevisionNotFoundException.apply(revisionId);
            }
         });
   }

   public CompletionStage<Revision> createRevision(User executor, Schema schema) {
      var revision = OpenRevision.apply(Operators.hash(), ActionMetadata.apply(executor), ActionMetadata.apply(executor), 0, schema);
      return repository
         .insertOrUpdateRevision(projectId, id, revision)
         .thenApply(d -> revision);
   }

   public CompletionStage<Records> download(User executor, String version) {
      return repository
         .findRevisionByVersion(projectId, id, version)
         .thenCompose(maybeRevision -> {
            if (maybeRevision.isPresent()) {
               var revision = maybeRevision.get();
               return store.get(getRevisionKey(revision.getId()));
            } else {
               throw VersionNotFoundException.apply(version);
            }
         });
   }

   public CompletionStage<List<CommittedRevision>> getVersions() {
      return repository
         .findAllRevisions(projectId, id)
         .thenApply(revisions -> revisions
            .stream()
            .filter(r -> r instanceof CommittedRevision)
            .map(r -> (CommittedRevision) r)
            .sorted(Comparator.comparing(CommittedRevision::getVersion).reversed())
            .collect(Collectors.toList()));
   }

   public CompletionStage<Done> upload(User executor, String revisionId, Records records) {
      return repository
         .findRevisionById(projectId, id, revisionId)
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
                        .insertOrUpdateRevision(projectId, id, revisionUpdated);
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

   private String getRevisionKey(String revisionId) {
      return String.format("%s/%s/%s", projectId, id, revisionId);
   }

}
