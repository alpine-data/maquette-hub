package maquette.datashop.specs.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import maquette.core.MaquetteRuntime;
import maquette.core.values.user.AuthenticatedUser;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.commands.CreateDataAssetCommand;
import maquette.datashop.commands.GetDataAssetCommand;
import maquette.datashop.commands.UpdateCustomDataAssetSettingsCommand;
import maquette.datashop.ports.FakeWorkspacesServicePort;
import maquette.datashop.providers.databases.Databases;
import maquette.datashop.providers.databases.commands.AnalyzeDatabaseCommand;
import maquette.datashop.providers.databases.commands.GetDatabaseConnectionCommand;
import maquette.datashop.providers.databases.commands.TestDatabaseConnectionCommand;
import maquette.datashop.providers.databases.model.*;
import maquette.datashop.providers.databases.ports.DatabaseAnalysisQueryResult;
import maquette.datashop.providers.databases.ports.DatabaseAnalysisResult;
import maquette.datashop.providers.databases.services.DatabaseServices;
import maquette.datashop.providers.datasets.records.Records;
import maquette.datashop.values.metadata.DataClassification;
import maquette.datashop.values.metadata.DataVisibility;
import maquette.datashop.values.metadata.DataZone;
import maquette.datashop.values.metadata.PersonalInformation;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Slf4j
public class DatabaseStepDefinitions extends DataAssetStepDefinitions {

    private final DatabaseServices databasesServices;
    private DatabaseSessionSettings givenDatabaseSessionSettings;

    private final List<DatabaseQuerySettings> givenDatabaseQuerySettings;

    private DatabaseSessionSettings mentionedDatabaseSessionSettings;

    private List<DatabaseQuerySettings> mentionedDatabaseQuerySettings;

    private final List<Records> records;

    private boolean givenAllowCustomQueries = false;

    private boolean givenAllowLocalSession = false;


    public DatabaseStepDefinitions(MaquetteRuntime runtime, FakeWorkspacesServicePort workspaces) {
        super(runtime, workspaces, Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(),
            Lists.newArrayList(), null, null);
        this.givenDatabaseQuerySettings = Lists.newArrayList();
        this.mentionedDatabaseQuerySettings = Lists.newArrayList();
        this.records = Lists.newArrayList();

        this.databasesServices = this.runtime
            .getModule(MaquetteDataShop.class)
            .getProviders()
            .getByType(Databases.class)
            .getServices();
    }

    public void $_creates_a_$_database(AuthenticatedUser user, String name) {
        try {
            var customSettings = DatabaseSettings.apply(givenDatabaseSessionSettings, givenDatabaseQuerySettings,
                givenAllowCustomQueries, givenAllowLocalSession);
            var om = runtime
                .getObjectMapperFactory()
                .createJsonMapper();
            var result = CreateDataAssetCommand
                .apply(Databases.TYPE_NAME, name, name, "Some nice speaking summary.", DataVisibility.PRIVATE,
                    DataClassification.PUBLIC, PersonalInformation.NONE, DataZone.RAW, user
                        .getId()
                        .getValue(), null, null, om.convertValue(customSettings, JsonNode.class))
                .run(user, runtime)
                .toCompletableFuture()
                .get()
                .toPlainText(runtime);

            mentionedAssets.add(name);
            mentionedUsers.add(user);
            results.add(result);
        } catch (Exception e) {
            log.error("Error", e);
            this.exception = e;
        }
    }

    public void $_updates_a_$_database(AuthenticatedUser user, String name) {
        try {
            var customSettings = DatabaseSettings.apply(givenDatabaseSessionSettings, givenDatabaseQuerySettings,
                givenAllowCustomQueries, givenAllowLocalSession);
            var om = runtime
                .getObjectMapperFactory()
                .createJsonMapper();
            UpdateCustomDataAssetSettingsCommand
                .apply(name, om.convertValue(customSettings, JsonNode.class))
                .run(user, this.runtime)
                .toCompletableFuture()
                .get();
        } catch (Exception e) {
            log.error("Error", e);
            this.exception = e;
        }
    }

    public void we_want_to_use_a_$_database(DatabaseDriver driver) {
        this.givenDatabaseSessionSettings = DatabaseSessionSettings.apply(driver, "connection", "username", "password");
    }

    public void we_add_a_query_with_$_name_and_$_query(String name, String query) {
        this.givenDatabaseQuerySettings.add(DatabaseQuerySettings.apply(name, query));
    }

    public void the_user_can_access_$_database(String databaseName) throws ExecutionException, InterruptedException {
        var result = GetDataAssetCommand
            .apply(databaseName)
            .run(mentionedUsers.get(mentionedUsers.size() - 1), this.runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(this.runtime);
        results.add(result);

        mentionedAssets.add(databaseName);

        var databaseSettings = this.databasesServices
            .getDatabaseSettings(mentionedUsers.get(mentionedUsers.size() - 1), databaseName)
            .toCompletableFuture()
            .get();

        this.mentionedDatabaseQuerySettings = databaseSettings.getQuerySettings();
        this.mentionedDatabaseSessionSettings = databaseSettings.getSessionSettings();
    }

    public void it_has_$_driver(DatabaseDriver driver) {
        assertThat(this.mentionedDatabaseSessionSettings.getDriver()).isEqualTo(driver);
    }

    public void it_has_$_query_name_with_$_query(String name, String query) {
        assertThat(this.mentionedDatabaseQuerySettings)
            .extracting(DatabaseQuerySettings::getName, DatabaseQuerySettings::getQuery)
            .containsAnyOf(tuple(name, query));
    }

    public void we_allow_custom_queries() {
        this.givenAllowCustomQueries = true;
    }

    public void we_allow_local_session() {
        this.givenAllowLocalSession = true;
    }

    public void records_output_is_not_empty() {
        assertThat(records).isNotEmpty();
    }

    public void $_tests_connection(AuthenticatedUser user) throws ExecutionException, InterruptedException {
        var connection = this.givenDatabaseSessionSettings;
        var firstQuery = this.givenDatabaseQuerySettings.get(0);
        var result = TestDatabaseConnectionCommand
            .apply(connection.getDriver(), connection.getConnection(), connection.getUsername(),
                connection.getPassword(), firstQuery.getQuery())
            .run(user, this.runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(this.runtime);
        results.add(result);
    }

    public void $_executes_$_query(AuthenticatedUser user, String queryName) {
        try {
            var result = this.databasesServices.executeQueryByName(user,
                mentionedAssets.get(mentionedAssets.size() - 1), queryName);
            records.add(result
                .toCompletableFuture()
                .get());
        } catch (Exception e) {
            log.error("Error", e);
            this.exception = e;
        }
    }

    public void $_executes_$_custom_query(AuthenticatedUser user, String customQuery) {
        try {
            var result = this.databasesServices.executeCustomQuery(user,
                mentionedAssets.get(mentionedAssets.size() - 1), customQuery);
            records.add(result
                .toCompletableFuture()
                .get());
        } catch (Exception e) {
            this.exception = e;
        }
    }

    public void $_downloads_connection_properties(AuthenticatedUser user) {
        try {
            var result = GetDatabaseConnectionCommand
                .apply(mentionedAssets.get(mentionedAssets.size() - 1))
                .run(user, this.runtime)
                .toCompletableFuture()
                .get()
                .toPlainText(this.runtime);
            results.add(result);
        } catch (Exception e) {
            log.error("Error", e);
            this.exception = e;
        }
    }

    public void $_analyzes_the_database(AuthenticatedUser user) {
        try {
            var result = AnalyzeDatabaseCommand
                .apply(mentionedAssets.get(mentionedAssets.size() - 1))
                .run(user, this.runtime)
                .toCompletableFuture()
                .get()
                .toPlainText(this.runtime);
            results.add(result);
        } catch (Exception e) {
            log.error("Error", e);
            this.exception = e;
        }
    }

    public void the_user_can_view_analysis_results() {
        the_output_should_contain("test-analysis-results");
    }
}
