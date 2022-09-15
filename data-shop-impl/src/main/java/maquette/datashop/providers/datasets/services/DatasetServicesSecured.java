package maquette.datashop.providers.datasets.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.datashop.providers.datasets.model.CommittedRevision;
import maquette.datashop.providers.datasets.model.DatasetVersion;
import maquette.datashop.providers.datasets.model.Revision;
import maquette.datashop.providers.datasets.records.Records;
import maquette.datashop.services.DataAssetServicesCompanion;
import maquette.datashop.values.access.DataAssetPermissions;
import org.apache.avro.Schema;

import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DatasetServicesSecured implements DatasetServices {

    private final DatasetServices delegate;

    private final DataAssetServicesCompanion comp;

    @Override
    public CompletionStage<Done> analyze(User executor, String dataset, DatasetVersion version) {
        return comp
            .withAuthorization(() -> comp.hasPermission(executor, dataset, DataAssetPermissions::canChangeSettings))
            .thenCompose(ok -> delegate.analyze(executor, dataset, version));
    }

    @Override
    public CompletionStage<CommittedRevision> commit(User executor, String dataset, UID revision, String message) {
        return comp
            .withAuthorization(() -> comp.hasPermission(executor, dataset, DataAssetPermissions::canProduce))
            .thenCompose(ok -> delegate.commit(executor, dataset, revision, message));
    }

    @Override
    public CompletionStage<Revision> create(User executor, String dataset, Schema schema) {
        return comp
            .withAuthorization(() -> comp.hasPermission(executor, dataset, DataAssetPermissions::canProduce))
            .thenCompose(ok -> delegate.create(executor, dataset, schema));
    }

    @Override
    public CompletionStage<Records> download(User executor, String dataset, DatasetVersion version) {
        return comp
            .withAuthorization(
                () -> comp.hasPermission(executor, dataset, DataAssetPermissions::canConsume),
                () -> comp.isSuperUser(executor),
                () -> comp.isSubscribedConsumer(executor, dataset))
            .thenCompose(ok -> delegate.download(executor, dataset, version));
    }

    @Override
    public CompletionStage<Records> download(User executor, String dataset) {
        return comp
            .withAuthorization(
                () -> comp.hasPermission(executor, dataset, DataAssetPermissions::canConsume),
                () -> comp.isSuperUser(executor),
                () -> comp.isSubscribedConsumer(executor, dataset))
            .thenCompose(ok -> delegate.download(executor, dataset));
    }

    @Override
    public CompletionStage<CommittedRevision> getCommit(User executor, String dataset, DatasetVersion version) {
        return comp
            .withAuthorization(
                () -> comp.hasPermission(executor, dataset, DataAssetPermissions::canConsume),
                () -> comp.isSuperUser(executor),
                () -> comp.isSubscribedConsumer(executor, dataset))
            .thenCompose(ok -> delegate.getCommit(executor, dataset, version));
    }

    @Override
    public CompletionStage<List<CommittedRevision>> listCommits(User executor, String dataset) {
        return comp
            .withAuthorization(
                () -> comp.hasPermission(executor, dataset, DataAssetPermissions::canConsume),
                () -> comp.isSuperUser(executor),
                () -> comp.isSubscribedConsumer(executor, dataset))
            .thenCompose(ok -> delegate.listCommits(executor, dataset));
    }

    @Override
    public CompletionStage<Done> upload(User executor, String dataset, UID revision, Records records) {
        return comp
            .withAuthorization(() -> comp.hasPermission(executor, dataset, DataAssetPermissions::canConsume))
            .thenCompose(ok -> delegate.upload(executor, dataset, revision, records));
    }

}
