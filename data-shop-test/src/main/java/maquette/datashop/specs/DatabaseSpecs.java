package maquette.datashop.specs;

import maquette.core.MaquetteRuntime;
import maquette.core.ports.email.FakeEmailClient;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.ports.FakeWorkspacesServicePort;
import maquette.datashop.providers.FakeProvider;
import maquette.datashop.providers.datasets.Datasets;
import maquette.datashop.providers.datasets.ports.DatasetsRepository;
import maquette.datashop.providers.datasets.ports.InMemoryDatasetDataExplorer;
import maquette.datashop.providers.datasets.records.Records;
import maquette.datashop.specs.steps.DatasetStepDefinitions;
import maquette.testutils.MaquetteContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public abstract class DatabaseSpecs {

    private DatasetStepDefinitions steps;

    private MaquetteContext context;

    @BeforeEach
    public void setup() {
        this.context = MaquetteContext.apply();

        var runtime = MaquetteRuntime.apply();
        var workspaces = FakeWorkspacesServicePort.apply();
        var datasets = Datasets.apply(setupDatasetsRepository(), InMemoryDatasetDataExplorer.apply(), workspaces);
        var shop = MaquetteDataShop.apply(setupDataAssetsRepository(), workspaces, FakeEmailClient.apply(),
            FakeProvider.apply(), datasets);

        var maquette = runtime
            .withModule(shop)
            .initialize(context.system, context.app);

        this.steps = new DatasetStepDefinitions(maquette, workspaces);
    }

    @AfterEach
    public void clean() {
        this.context.clean();
    }

    public abstract DataAssetsRepository setupDataAssetsRepository();

    public abstract DatasetsRepository setupDatasetsRepository();

    /**
     * Create pretty case with usual data
     */
    @Test
    public void create() throws ExecutionException, InterruptedException {

    }

    /**
     * Create database with an empty query to verify validation
     */
    @Test
    public void createWithEmptyQuery() throws ExecutionException, InterruptedException {

    }

    /**
     * Create database with and without test flag and check if the connection is valid on test flag enabled
     * multiple queries, test executed for each
     */
    @Test
    public void createWithTestFlag() throws ExecutionException, InterruptedException {

    }

    /**
     * Create db with checkbox validation
     */
    @Test
    public void createWithAllowCustomQueries() throws ExecutionException, InterruptedException {

    }

    /**
     * Create db with at least one query
     */
    @Test
    public void createWithAtLeastOneQuery() throws ExecutionException, InterruptedException {

    }

    /**
     * Analyze the named queries which sends a request to data explorer and stores the response in the properties of a data asset
     */
    @Test
    public void analyze() throws ExecutionException, InterruptedException {

    }

    /**
     * Download one of the named queries of a database
     */
    @Test
    public void download() throws ExecutionException, InterruptedException {

    }

    /**
     * Download custom queries and test error message if not allowed
     * Disallow execution of custom queries, if option "allowCustomQueries" is disabled
     */
    @Test
    public void downloadCustom() throws ExecutionException, InterruptedException {

    }

    /**
     * Download session properties and test error message if not allowed
     * Disallow getting of database credentials, if "allowLocalSession" flag is disabled
     */
    @Test
    public void downloadConnectionProperies() throws ExecutionException, InterruptedException {

    }

    /**
     * Database named queries names must be unique
     */
    @Test
    public void createUniqueNames() throws ExecutionException, InterruptedException {
    }
}
