package maquette.datashop.specs;

import com.fasterxml.jackson.core.JsonProcessingException;
import maquette.core.MaquetteRuntime;
import maquette.core.ports.email.FakeEmailClient;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.ports.FakeWorkspacesServicePort;
import maquette.datashop.providers.FakeProvider;
import maquette.datashop.providers.databases.Databases;
import maquette.datashop.providers.databases.model.DatabaseDriver;
import maquette.datashop.providers.databases.ports.InMemoryDatabasePortImpl;
import maquette.datashop.providers.datasets.ports.InMemoryDatabaseDataExplorer;
import maquette.datashop.specs.steps.DatabaseStepDefinitions;
import maquette.testutils.MaquetteContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public abstract class DatabaseSpecs {

    private DatabaseStepDefinitions steps;

    private MaquetteContext context;

    @BeforeEach
    public void setup() {
        this.context = MaquetteContext.apply();

        var runtime = MaquetteRuntime.apply();
        var workspaces = FakeWorkspacesServicePort.apply();
        var databases = Databases.apply(InMemoryDatabasePortImpl.apply(), InMemoryDatabaseDataExplorer.apply(),
            workspaces);
        var shop = MaquetteDataShop.apply(setupDataAssetsRepository(), workspaces, FakeEmailClient.apply(),
            FakeProvider.apply(), databases);

        var maquette = runtime
            .withModule(shop)
            .initialize(context.system, context.app);

        this.steps = new DatabaseStepDefinitions(maquette, workspaces);
    }

    @AfterEach
    public void clean() {
        this.context.clean();
    }

    public abstract DataAssetsRepository setupDataAssetsRepository();

    /**
     * Create pretty case with usual data, verify settings and access rights
     */
    @Test
    public void create() throws ExecutionException, InterruptedException, JsonProcessingException {
        // given
        steps.we_want_to_use_a_$_database(DatabaseDriver.POSTGRESQL);
        steps.we_add_a_query_with_$_name_and_$_query("All customers", "select * from customer");
        steps.we_allow_custom_queries();
        steps.we_allow_local_session();

        // when
        steps.$_creates_a_$_database(context.users.bob, "my_database");

        // then
        steps.the_user_can_access_$_database("my_database");
        steps.it_has_$_driver(DatabaseDriver.POSTGRESQL);
        steps.it_has_$_query_name_with_$_query("All customers", "select * from customer");

        // and password is obfuscated
        steps.the_output_should_contain("***");

        // when
        steps.$_browses_all_data_assets(context.users.charly);

        // Then
        steps.the_output_should_not_contain("my_database");
    }

    /**
     * Create database with an empty query to verify validation
     */
    @Test
    public void createWithEmptyQuery() {
        // given
        steps.we_want_to_use_a_$_database(DatabaseDriver.POSTGRESQL);

        // when
        steps.$_creates_a_$_database(context.users.bob, "my_database");

        // then
        steps.an_error_occurs_with_a_message_$("A Database data asset requires at least one query.");
    }

    /**
     * Test connection
     */
    @Test
    public void testConnection() throws ExecutionException, InterruptedException {
        // given
        steps.we_want_to_use_a_$_database(DatabaseDriver.POSTGRESQL);
        steps.we_add_a_query_with_$_name_and_$_query("All customers", "select * from customer");

        // when
        steps.$_tests_connection(context.users.bob);

        // then
        steps.the_output_should_contain("success");
    }

    /**
     * Test allow custom queries validation & execution
     */
    @Test
    public void createAndExecuteAllowCustomQueries() {
        // given
        steps.we_want_to_use_a_$_database(DatabaseDriver.POSTGRESQL);
        steps.we_add_a_query_with_$_name_and_$_query("All customers", "select * from customer");

        // when
        steps.$_creates_a_$_database(context.users.bob, "my_database");
        steps.$_executes_$_custom_query(context.users.bob, "select * from test");

        // then
        steps.an_error_occurs_with_a_message_$("Custom queries are only allowed when allow custom queries is true.");

        // given
        steps.we_allow_custom_queries();

        // when
        steps.$_updates_a_$_database(context.users.bob, "my_database");
        steps.$_executes_$_custom_query(context.users.bob, "select * from test");

        // then
        steps.records_output_is_not_empty();
    }

    /**
     * Test local session execution
     */
    @Test
    public void createWithAllowLocalSession() throws ExecutionException, InterruptedException {
        // given
        steps.we_want_to_use_a_$_database(DatabaseDriver.POSTGRESQL);
        steps.we_add_a_query_with_$_name_and_$_query("All customers", "select * from customer");
        steps.we_allow_local_session();

        // when
        steps.$_creates_a_$_database(context.users.bob, "my_database");

        // then
        steps.an_error_occurs_with_a_message_$(
            "Allow local sessions is only allowed when custom queries are allowed as well.");

        // given
        steps.we_allow_custom_queries();

        // when
        steps.$_creates_a_$_database(context.users.bob, "my_database");

        // then
        steps.the_user_can_access_$_database("my_database");
    }

    /**
     * Analyze named queries.
     * This sends a request to a data explorer and stores the response in the properties of a data asset.
     */
    @Test
    public void analyze() throws ExecutionException, InterruptedException {
        // given
        steps.we_want_to_use_a_$_database(DatabaseDriver.POSTGRESQL);
        steps.we_add_a_query_with_$_name_and_$_query("All customers", "select * from customer");
        steps.we_allow_custom_queries();
        steps.we_allow_local_session();

        // when
        steps.$_creates_a_$_database(context.users.bob, "my_database");
        steps.$_analyzes_the_database(context.users.bob);

        // then
        steps.the_user_can_access_$_database("my_database");
        steps.the_user_can_view_analysis_results();
    }

    /**
     * Download one of the named queries of a database
     */
    @Test
    public void executeNamedQuery() {
        // given
        steps.we_want_to_use_a_$_database(DatabaseDriver.POSTGRESQL);
        steps.we_add_a_query_with_$_name_and_$_query("All customers", "select * from customer");

        // when
        steps.$_creates_a_$_database(context.users.bob, "my_database");
        steps.$_executes_$_query(context.users.bob, "All customers");

        // then
        steps.records_output_is_not_empty();

        // when
        steps.$_executes_$_query(context.users.bob, "Non existing");

        // then
        steps.an_error_occurs_with_a_message_$("No query found with name `Non existing`.");
    }

    /**
     * Download session properties and test error message if not allowed.
     * Disallow getting of database credentials, if "allowLocalSession" flag is disabled
     */
    @Test
    public void downloadConnectionProperies() {
        // given
        steps.we_want_to_use_a_$_database(DatabaseDriver.POSTGRESQL);
        steps.we_add_a_query_with_$_name_and_$_query("All customers", "select * from customer");

        // when
        steps.$_creates_a_$_database(context.users.bob, "my_database");
        steps.$_downloads_connection_properties(context.users.bob);

        // then
        steps.an_error_occurs_with_a_message_$("Using local sessions is not allowed.");

        // given
        steps.we_allow_custom_queries();
        steps.we_allow_local_session();

        // when
        steps.$_updates_a_$_database(context.users.bob, "my_database");
        steps.$_downloads_connection_properties(context.users.bob);

        // then
        steps.the_output_should_contain("password");

        // when
        steps.$_downloads_connection_properties(context.users.charly);

        // then
        steps.an_error_occurs_with_a_message_$("You are not authorized to execute this action.");
    }

    /**
     * Database named queries names must be unique
     */
    @Test
    public void createUniqueNames() {
        // given
        steps.we_want_to_use_a_$_database(DatabaseDriver.POSTGRESQL);
        steps.we_add_a_query_with_$_name_and_$_query("All customers", "select * from customer");
        steps.we_add_a_query_with_$_name_and_$_query("All customers", "select * from customer");

        // when
        steps.$_creates_a_$_database(context.users.bob, "my_database");

        // then
        steps.an_error_occurs_with_a_message_$("All queries must have a unique name for the Database Data Asset.");
    }

    /**
     * Database named queries names must be unique during update
     */
    @Test
    public void updateUniqueNames() {
        // given
        steps.we_want_to_use_a_$_database(DatabaseDriver.POSTGRESQL);
        steps.we_add_a_query_with_$_name_and_$_query("All customers", "select * from customer");
        steps.we_add_a_query_with_$_name_and_$_query("All customers2", "select * from customer");

        // when
        steps.$_creates_a_$_database(context.users.bob, "my_database");

        // given
        steps.we_add_a_query_with_$_name_and_$_query("All customers", "select * from customer");

        // when
        steps.$_updates_a_$_database(context.users.bob, "my_database");

        // then
        steps.an_error_occurs_with_a_message_$("All queries must have a unique name for the Database Data Asset.");
    }
}
