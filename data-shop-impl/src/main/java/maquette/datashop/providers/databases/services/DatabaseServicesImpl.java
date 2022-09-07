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
import maquette.datashop.providers.databases.model.DatabaseProperties;
import maquette.datashop.providers.databases.ports.DatabaseAnalysisResult;
import maquette.datashop.providers.datasets.records.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DatabaseServicesImpl implements DatabaseServices {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseServices.class);

    private final DataAssetEntities assets;

    private final UserEntities users;

    private final DatabaseEntities databases;

    @Override
    public CompletionStage<Done> analyze(User executor, String database) {
        if (executor instanceof AuthenticatedUser) {
            return users
                .getUserById(((AuthenticatedUser) executor).getId())
                .thenCompose(UserEntity::getAuthenticationToken)
                .thenCompose(authToken -> assets
                    .getByName(database)
                    .thenApply(asset -> {
                        databases.analyze(asset, authToken.getId().getValue(), authToken.getSecret());
                        return Done.getInstance();
                    }));
        } else {
            LOG.warn("Analyze has been called by a not authenticated user. Cannot initiate analysis.");
            return CompletableFuture.completedFuture(Done.getInstance());
        }
    }

    @Override
    public CompletionStage<Records> executeQueryById(User executor, String database, String queryId) {
        return assets
            .getByName(database)
            .thenCompose(entity -> databases.executeQueryById(entity, executor, queryId));
    }

    @Override
    public CompletionStage<Records> executeQueryByName(User executor, String database, String queryName) {
        return assets
            .getByName(database)
            .thenCompose(entity -> databases.executeQueryByName(entity, executor, queryName));
    }

    @Override
    public CompletionStage<Records> executeCustomQuery(User executor, String database, String query) {
        return assets
            .getByName(database)
            .thenCompose(entity -> databases.executeQueryByName(entity, executor, query));
    }

    @Override
    public CompletionStage<ConnectionTestResult> test(DatabaseDriver driver, String connection, String username,
                                                      String password, String query) {
        return databases.test(driver, connection, username, password, query);
    }

    @Override
    public CompletionStage<Optional<DatabaseAnalysisResult>> getAnalysisResult(User executor, String database) {
        return assets
            .getByName(database)
            .thenCompose(entity -> entity.getCustomSettings(DatabaseProperties.class))
            .thenApply(DatabaseProperties::getQueryStatistics);
    }

}
