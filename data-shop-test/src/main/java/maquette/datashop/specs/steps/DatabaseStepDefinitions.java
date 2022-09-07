package maquette.datashop.specs.steps;

import com.google.common.collect.Lists;
import maquette.core.MaquetteRuntime;
import maquette.core.values.user.AuthenticatedUser;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.ports.FakeWorkspacesServicePort;
import maquette.datashop.providers.databases.Databases;
import maquette.datashop.providers.datasets.Datasets;
import maquette.datashop.providers.datasets.commands.ListVersionsCommand;
import maquette.datashop.providers.datasets.model.DatasetVersion;
import maquette.datashop.providers.datasets.records.Records;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseStepDefinitions extends DataAssetStepDefinitions {

    private final List<Records> uploaded;

    private final List<Records> downloaded;

    public DatabaseStepDefinitions(MaquetteRuntime runtime, FakeWorkspacesServicePort workspaces) {
        super(runtime, workspaces, Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(), null);
        this.uploaded = Lists.newArrayList();
        this.downloaded = Lists.newArrayList();
    }

    public void $_uploads_records_$_to_dataset_$(AuthenticatedUser user, Records records, String dataset) throws ExecutionException, InterruptedException {
        var databases = this
            .runtime
            .getModule(MaquetteDataShop.class)
            .getProviders()
            .getByType(Databases.class);
    }

    public void $_uploads_records_to_dataset_$(AuthenticatedUser user, String dataset) throws ExecutionException, InterruptedException {
        var records = Records.getSamples().getCountryRecords();
        $_uploads_records_$_to_dataset_$(user, records, dataset);
    }

    public void $_uploads_different_records_to_dataset_$(AuthenticatedUser user, String dataset) throws ExecutionException, InterruptedException {
        var records = Records.getSamples().getCityRecords();
        $_uploads_records_$_to_dataset_$(user, records, dataset);
    }

    public void $_lists_versions_of_data_asset_$(AuthenticatedUser user, String dataset) throws ExecutionException,
        InterruptedException {
        var output = ListVersionsCommand
            .apply(dataset)
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        mentionedAssets.add(dataset);
        results.add(output);
    }

    public void $_downloads_version_$_from_dataset_$(AuthenticatedUser user, String version, String dataset) throws ExecutionException, InterruptedException {
        var result = runtime
            .getModule(MaquetteDataShop.class)
            .getProviders()
            .getByType(Datasets.class)
            .getServices()
            .download(user, dataset, DatasetVersion.apply(version))
            .toCompletableFuture()
            .get();

        downloaded.add(result);
    }

    public void the_uploaded_records_should_equal_the_downloaded_records() {
        var uploaded = this.uploaded.get(this.uploaded.size() -  1);
        var downloaded = this.downloaded.get(this.downloaded.size() - 1);

        assertThat(uploaded.getRecords()).containsAll(downloaded.getRecords());
    }

    public void $_downloads_latest_version_from_dataset_$(AuthenticatedUser user, String dataset) throws ExecutionException, InterruptedException {
        var result = runtime
            .getModule(MaquetteDataShop.class)
            .getProviders()
            .getByType(Datasets.class)
            .getServices()
            .download(user, dataset)
            .toCompletableFuture()
            .get();

        downloaded.add(result);
    }
}
