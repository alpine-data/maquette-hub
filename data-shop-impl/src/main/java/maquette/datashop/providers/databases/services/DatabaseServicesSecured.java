package maquette.datashop.providers.databases.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.values.user.User;
import maquette.datashop.entities.DataAssetEntities;
import maquette.datashop.providers.databases.model.ConnectionTestResult;
import maquette.datashop.providers.databases.model.DatabaseDriver;
import maquette.datashop.providers.databases.model.DatabaseSettings;
import maquette.datashop.providers.databases.ports.DatabaseAnalysisResult;
import maquette.datashop.providers.datasets.records.Records;
import maquette.datashop.services.DataAssetServicesCompanion;
import maquette.datashop.values.access.DataAssetPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DatabaseServicesSecured implements DatabaseServices {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseServices.class);

    private final DataAssetEntities assets;

    private final DatabaseServices delegate;

    private final DataAssetServicesCompanion comp;

    @Override
    public CompletionStage<Done> analyze(User executor, String database) {
        return comp
            .withAuthorization(() -> comp.hasPermission(executor, database, DataAssetPermissions::canChangeSettings))
            .thenCompose(ok -> delegate.analyze(executor, database));
    }

    @Override
    public CompletionStage<Records> executeQueryById(User executor, String database, String queryId) {
        return comp
            .withAuthorization(
                () -> comp.hasPermission(executor, database, DataAssetPermissions::canConsume),
                () -> comp.isSuperUser(executor),
                () -> comp.isSubscribedConsumer(executor, database))
            .thenCompose(ok -> delegate.executeQueryById(executor, database, queryId));
    }

    @Override
    public CompletionStage<Records> executeQueryByName(User executor, String database, String queryName) {
        return comp
            .withAuthorization(
                () -> comp.hasPermission(executor, database, DataAssetPermissions::canConsume),
                () -> comp.isSuperUser(executor),
                () -> comp.isSubscribedConsumer(executor, database))
            .thenCompose(ok -> delegate.executeQueryById(executor, database, queryName));
    }

    @Override
    public CompletionStage<Records> executeCustomQuery(User executor, String database, String query) {
        return comp
            .withAuthorization(
                () -> comp.hasPermission(executor, database, DataAssetPermissions::canConsume),
                () -> comp.isSuperUser(executor),
                () -> comp.isSubscribedConsumer(executor, database),
                () -> assets
                    .getByName(database)
                    .thenCompose(entity -> entity.getCustomSettings(DatabaseSettings.class))
                    .thenApply(DatabaseSettings::isAllowCustomQueries))
            .thenCompose(ok -> delegate.executeCustomQuery(executor, database, query));
    }

    @Override
    public CompletionStage<ConnectionTestResult> test(DatabaseDriver driver, String connection, String username,
                                                      String password, String query) {
        return delegate.test(driver, connection, username, password, query);
    }

    @Override
    public CompletionStage<Optional<DatabaseAnalysisResult>> getAnalysisResult(User executor, String database) {
        return delegate.getAnalysisResult(executor, database);
    }

    @Override
    public CompletionStage<DatabaseSettings> getDatabaseSettings(User executor, String database) {
        // TODO: Check Permission
        return delegate.getDatabaseSettings(executor, database);
    }

}
