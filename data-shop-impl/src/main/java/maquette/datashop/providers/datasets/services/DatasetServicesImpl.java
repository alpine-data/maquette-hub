package maquette.datashop.providers.datasets.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.datashop.entities.DataAssetEntities;
import maquette.datashop.providers.datasets.ports.DatasetDataExplorer;
import maquette.datashop.providers.datasets.DatasetEntity;
import maquette.datashop.providers.datasets.ports.DatasetsRepository;
import maquette.datashop.providers.datasets.model.CommittedRevision;
import maquette.datashop.providers.datasets.model.DatasetVersion;
import maquette.datashop.providers.datasets.model.Revision;
import maquette.datashop.providers.datasets.records.Records;
import org.apache.avro.Schema;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DatasetServicesImpl implements DatasetServices {

    private final DatasetsRepository repository;

    private final DatasetDataExplorer explorer;

    private final DataAssetEntities assets;

    @Override
    public CompletionStage<Done> analyze(User executor, String dataset, DatasetVersion version) {
        return getEntity(dataset).thenCompose(entity -> entity.analyze(version));
    }

    @Override
    public CompletionStage<CommittedRevision> commit(User executor, String dataset, UID revision, String message) {
        var entityCS = getEntity(dataset);

        return entityCS
            .thenCompose(entity -> entity.commit(executor, revision, message))
            .thenApply(rev -> {
                entityCS.thenApply(entity -> entity.analyze(rev.getVersion()));
                return rev;
            });
    }

    @Override
    public CompletionStage<Revision> create(User executor, String dataset, Schema schema) {
        return getEntity(dataset).thenCompose(entity -> entity.createRevision(executor, schema));
    }

    @Override
    public CompletionStage<Records> download(User executor, String dataset, DatasetVersion version) {
        return getEntity(dataset).thenCompose(entity -> entity.download(executor, version));
    }

    @Override
    public CompletionStage<Records> download(User executor, String dataset) {
        return getEntity(dataset).thenCompose(entity -> entity.download(executor));
    }

    @Override
    public CompletionStage<Done> upload(User executor, String dataset, UID revision, Records records) {
        return getEntity(dataset).thenCompose(entity -> entity.upload(executor, revision, records));
    }

    private CompletionStage<DatasetEntity> getEntity(String dataset) {
        return assets
            .getByName(dataset)
            .thenApply(entity -> DatasetEntity.apply(repository, explorer, entity));
    }

}
