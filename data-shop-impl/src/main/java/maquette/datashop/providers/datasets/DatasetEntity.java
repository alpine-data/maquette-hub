package maquette.datashop.providers.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.datashop.entities.DataAssetEntity;
import maquette.datashop.providers.datasets.exceptions.RevisionNotFoundException;
import maquette.datashop.providers.datasets.exceptions.VersionNotFoundException;
import maquette.datashop.providers.datasets.model.CommittedRevision;
import maquette.datashop.providers.datasets.model.DatasetVersion;
import maquette.datashop.providers.datasets.model.OpenRevision;
import maquette.datashop.providers.datasets.model.Revision;
import maquette.datashop.providers.datasets.ports.DatasetDataExplorer;
import maquette.datashop.providers.datasets.ports.DatasetsRepository;
import maquette.datashop.providers.datasets.records.Records;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DatasetEntity {

    private static final Logger LOG = LoggerFactory.getLogger(DatasetEntity.class);

    private final DatasetsRepository repository;

    private final DatasetDataExplorer explorer;

    private final DataAssetEntity entity;

    public CompletionStage<Done> analyze(DatasetVersion version, String authTokenId, String authTokenSecret) {
        var revisionCS = repository
            .findRevisionByVersion(entity.getId(), version)
            .thenApply(opt -> opt.orElseThrow(() -> VersionNotFoundException.apply(version)));

        var datasetCS = entity.getProperties();

        return Operators
            .compose(
                revisionCS, datasetCS,
                (revision, dataset) -> explorer
                    .analyze(dataset
                        .getMetadata()
                        .getName(), revision.getVersion(), authTokenId, authTokenSecret)
                    .thenApply(revision::withStatistics)
                    .thenCompose(r -> repository.insertOrUpdateRevision(entity.getId(), r))
                    .thenApply(done -> {
                        LOG.info("Successfully analyzed dataset `{}` version `{}`", dataset.getMetadata().getName(), version);
                        return done;
                    }))
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
                                revision.getId(), revision.getCreated(), ActionMetadata.apply(executor),
                                ActionMetadata.apply(executor),
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
        var revision = OpenRevision.apply(UID.apply(), ActionMetadata.apply(executor), ActionMetadata.apply(executor)
            , 0, schema);
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
                    return repository
                        .getRecordsStore(entity.getId())
                        .get(revision
                            .getId()
                            .getValue());
                } else {
                    throw VersionNotFoundException.apply(version.toString());
                }
            });
    }

    public CompletionStage<Records> download(User executor) {
        return getVersions()
            .thenApply(versions -> versions
                .stream()
                .map(CommittedRevision::getVersion)
                .findFirst()
                .orElse(DatasetVersion.apply("1.0.0")))
            .thenCompose(version -> download(executor, version));
    }

    public DataAssetEntity getEntity() {
        return entity;
    }

    public CompletionStage<CommittedRevision> getVersion(DatasetVersion version) {
        return repository
            .findRevisionByVersion(entity.getId(), version)
            .thenCompose(maybeRevision -> maybeRevision
                .<CompletionStage<CommittedRevision>>map(CompletableFuture::completedFuture)
                .orElseGet(() -> CompletableFuture.failedFuture(VersionNotFoundException.apply(version.toString()))));
    }

    public CompletionStage<List<CommittedRevision>> getVersions() {
        return repository
            .findAllRevisions(entity.getId())
            .thenApply(revisions -> revisions
                .stream()
                .filter(r -> r instanceof CommittedRevision)
                .map(r -> (CommittedRevision) r)
                .sorted(Comparator
                    .comparing(CommittedRevision::getVersion)
                    .reversed())
                .collect(Collectors.toList()));
    }

    public CompletionStage<Done> upload(User executor, UID revisionId, Records records) {
        return repository
            .findRevisionById(entity.getId(), revisionId)
            .thenCompose(maybeRevision -> {
                if (maybeRevision.isPresent()) {
                    var revision = maybeRevision.get();

                    return repository
                        .getRecordsStore(entity.getId())
                        .append(revision
                            .getId()
                            .getValue(), records)
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
                    var compatible = versions
                        .get(0)
                        .getSchema()
                        .getFields()
                        .stream()
                        .noneMatch(field -> {
                            Schema.Field existingField = schema.getField(field.name());
                            return !field.equals(existingField);
                        });

                    var version = versions
                        .get(0)
                        .getVersion();

                    if (compatible) {
                        return DatasetVersion.apply(version.getMajor(), version.getMinor() + 1, 0);
                    } else {
                        return DatasetVersion.apply(version.getMajor() + 1, 0, 0);
                    }
                }
            });
    }

}
