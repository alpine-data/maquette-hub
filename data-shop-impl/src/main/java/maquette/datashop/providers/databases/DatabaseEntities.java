package maquette.datashop.providers.databases;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.user.User;
import maquette.datashop.entities.DataAssetEntity;
import maquette.datashop.providers.databases.model.ConnectionTestResult;
import maquette.datashop.providers.databases.model.DatabaseDriver;
import maquette.datashop.providers.databases.model.DatabaseProperties;
import maquette.datashop.providers.databases.model.DatabaseSettings;
import maquette.datashop.providers.databases.ports.DatabaseDataExplorer;
import maquette.datashop.providers.databases.ports.DatabasePort;
import maquette.datashop.providers.datasets.records.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DatabaseEntities {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseEntities.class);

    private final DatabasePort database;

    private final DatabaseDataExplorer explorer;

    public CompletionStage<Done> analyze(DataAssetEntity entity, String authToken, String authTokenSecret) {
        var settingsCS = entity.getCustomSettings(DatabaseSettings.class);
        var dbPropertiesCS = entity.getCustomProperties(DatabaseProperties.class);
        var propertiesCS = entity.getProperties();

        return Operators.compose(
                settingsCS, dbPropertiesCS, propertiesCS,
                (settings, dbProperties, properties) -> explorer
                    .analyze(properties.getMetadata().getName(), authToken, authTokenSecret)
                    .thenApply(dbProperties::withQueryStatistics)
                    .thenCompose(entity::updateCustomProperties)
                    .thenApply(done -> {
                        LOG.info("Successfully analyzed database `{}`", properties.getMetadata().getName());
                        return done;
                    }))
            .thenCompose(cs -> cs);
    }

    public CompletionStage<Records> executeQueryById(DataAssetEntity entity, User executor, String queryId) {
        return entity
            .getCustomSettings(DatabaseSettings.class)
            .thenCompose(properties -> this.executeQueryById(properties, queryId));
    }

    public CompletionStage<Records> executeQueryById(DatabaseSettings properties, String queryId) {
        return database.read(
            properties.getDriver(),
            properties.getConnection(),
            properties.getUsername(),
            properties.getPassword(),
            properties.getQueryById(queryId).getQuery());
    }

    public CompletionStage<Records> executeQueryByName(DataAssetEntity entity, User executor, String queryName) {
        return entity
            .getCustomSettings(DatabaseSettings.class)
            .thenCompose(properties -> this.executeQueryByName(properties, queryName));
    }

    public CompletionStage<Records> executeQueryByName(DatabaseSettings properties, String queryName) {
        return database.read(
            properties.getDriver(),
            properties.getConnection(),
            properties.getUsername(),
            properties.getPassword(),
            properties.getQueryByName(queryName).getQuery());
    }

    public CompletionStage<Records> executeCustomQuery(DatabaseSettings properties, String query) {
        return database.read(
            properties.getDriver(),
            properties.getConnection(),
            properties.getUsername(),
            properties.getPassword(),
            query);
    }

    public CompletionStage<ConnectionTestResult> test(
        DatabaseDriver driver, String connection, String username, String password, String query) {

        return database.test(driver, connection, username, password, query);
    }

    public CompletionStage<ConnectionTestResult> test(DatabaseSettings properties) {
        return test(
            properties.getDriver(),
            properties.getConnection(),
            properties.getUsername(),
            properties.getPassword(),
            properties.getQuery().get(0).getQuery()); // TODO: Test all queries.
    }

}
