package maquette.datashop.providers.databases.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.modules.users.UserEntities;
import maquette.core.modules.users.UserEntity;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import maquette.datashop.entities.DataAssetEntities;
import maquette.datashop.providers.databases.DatabaseEntities;
import maquette.datashop.providers.databases.model.ConnectionTestResult;
import maquette.datashop.providers.databases.model.DatabaseDriver;
import maquette.datashop.providers.databases.ports.DatabaseAnalysisResult;
import maquette.datashop.providers.datasets.records.Records;
import maquette.datashop.services.DataAssetServicesCompanion;
import maquette.datashop.values.access.DataAssetPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DatabaseServicesSecured implements DatabaseServices {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseServices.class);

    private final DatabaseServices delegate;

    private final DataAssetServicesCompanion comp;

    @Override
    public CompletionStage<Done> analyze(User executor, String database) {
        return comp
            .withAuthorization(() -> comp.hasPermission(executor, database, DataAssetPermissions::canChangeSettings))
            .thenCompose(ok -> delegate.analyze(executor, database));
    }

    @Override
    public CompletionStage<Records> download(User executor, String database) {
        return comp
            .withAuthorization(
                () -> comp.hasPermission(executor, database, DataAssetPermissions::canConsume),
                () -> comp.isSuperUser(executor),
                () -> comp.isSubscribedConsumer(executor, database))
            .thenCompose(ok -> delegate.download(executor, database));
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

}
