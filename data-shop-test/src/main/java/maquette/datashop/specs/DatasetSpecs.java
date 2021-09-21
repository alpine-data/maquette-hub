package maquette.datashop.specs;

import maquette.core.MaquetteRuntime;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.providers.FakeProvider;
import maquette.datashop.providers.datasets.Datasets;
import maquette.datashop.providers.datasets.ports.DatasetsRepository;
import maquette.datashop.providers.datasets.ports.InMemoryDatasetDataExplorer;
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

        var maquette = MaquetteRuntime
            .apply()
            .withModule(shop)
            .initialize();

        this.steps = new DatasetStepDefinitions(maquette);
        this.context = MaquetteContext.apply();
    }

    abstract DataAssetsRepository setupDataAssetsRepository();

    abstract DatasetsRepository setupDatasetsRepository();

    @Test
    public void datasetVersioning() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_data_asset_of_type_$_with_name_$(context.users.bob, "dataset", "some-asset");

        // When
        steps.$_uploads_records_to_dataset_$(context.users.bob, "some-asset");
        steps.$_uploads_different_records_to_dataset_$(context.users.bob, "some-asset");
        steps.$_lists_versions_of_data_asset_$(context.users.bob, "some-asset");
    }

}
