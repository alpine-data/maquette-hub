package maquette.datashop.specs;

import io.javalin.Javalin;
import maquette.core.MaquetteRuntime;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.providers.FakeProvider;
import maquette.datashop.providers.datasets.Datasets;
import maquette.datashop.providers.datasets.ports.DatasetsRepository;
import maquette.datashop.providers.datasets.ports.InMemoryDatasetDataExplorer;
import maquette.datashop.providers.datasets.records.Records;
import maquette.datashop.specs.steps.DatasetStepDefinitions;
import maquette.testutils.MaquetteContext;
import maquette.workspaces.fake.FakeWorkspaceEntities;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public abstract class DatasetSpecs {

    private DatasetStepDefinitions steps;

    private MaquetteContext context;

    @Before
    public void setup() {
        var runtime = MaquetteRuntime.apply();
        var workspaces = FakeWorkspaceEntities.apply();
        var datasets = Datasets.apply(runtime, setupDatasetsRepository(), InMemoryDatasetDataExplorer.apply(), workspaces);
        var shop = MaquetteDataShop.apply(setupDataAssetsRepository(), workspaces, FakeProvider.apply(), datasets);

        var maquette = runtime
            .withApp(Javalin.create())
            .withModule(shop)
            .initialize();

        this.steps = new DatasetStepDefinitions(maquette);
        this.context = MaquetteContext.apply();
    }

    public abstract DataAssetsRepository setupDataAssetsRepository();

    public abstract DatasetsRepository setupDatasetsRepository();

    /**
     * Datasets are versioned automatically, based on their schema, following semantic versioning principles.
     * If schemas of consecutive versions are not compatible to each other, the major version is increased.
     * If schemas are compatible, the minor version is increased.
     *
     * The patch-version is currently never increased. Might be used in the future, e.g. if versions need to be modified to
     * be compliant with data protection laws (delete rows including personal data of a specific person who requested that).
     *
     * Schemas are compatible if the newer schema includes all fields of the previous schema.
     */
    @Test
    public void datasetVersioning() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_data_asset_of_type_$_with_name_$(context.users.bob, "dataset", "some-asset");

        /*
         * When
         * ...the user uploads two version with different incompatible schemas
         *
         * Note:
         * ...country records in the example have a field `capital`, while city records don`t have this field.
         */
        steps.$_uploads_records_$_to_dataset_$(context.users.bob, Records.getSamples().getCountryRecords(), "some-asset");
        steps.$_uploads_records_$_to_dataset_$(context.users.bob, Records.getSamples().getCityRecords(), "some-asset");
        steps.$_lists_versions_of_data_asset_$(context.users.bob, "some-asset");

        // Then
        steps.the_output_should_contain("1.0.0", "2.0.0");


        /*
         * When
         * ...the user uploads new versions with the same schema, but different to schema of version 2.0.0
         *
         * Note:
         * ... since county records have the same fields as city records, plus one additional field, the schemas are compatible.
         */
        steps.$_uploads_records_$_to_dataset_$(context.users.bob, Records.getSamples().getCountryRecords(), "some-asset");
        steps.$_lists_versions_of_data_asset_$(context.users.bob, "some-asset");

        // Then
        steps.the_output_should_contain("1.0.0", "2.0.0", "2.1.0");
    }

    /**
     * When reading data, the result should contain all records which have been written. The
     * order might be different.
     *
     * When reading data, one may specify a specific version. Specifying nothing will fetch the latest version.
     */
    @Test
    public void writeAndReadData() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_data_asset_of_type_$_with_name_$(context.users.bob, "dataset", "some-asset");

        // When
        steps.$_uploads_records_to_dataset_$(context.users.bob, "some-asset");
        steps.$_downloads_version_$_from_dataset_$(context.users.bob, "1.0.0", "some-asset");

        // Then
        steps.the_uploaded_records_should_equal_the_downloaded_records();
    }

    /**
     * If no version is specified when downloading data. The latest version will be used.
     */
    @Test
    public void readLatestVersion() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_data_asset_of_type_$_with_name_$(context.users.bob, "dataset", "some-asset");

        // When
        steps.$_uploads_records_to_dataset_$(context.users.bob, "some-asset");
        steps.$_uploads_different_records_to_dataset_$(context.users.bob, "some-asset");
        steps.$_downloads_latest_version_from_dataset_$(context.users.bob, "some-asset");

        //
        steps.the_uploaded_records_should_equal_the_downloaded_records();
    }

}
