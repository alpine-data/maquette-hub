package maquette.datashop.specs.steps;

import com.google.common.collect.Lists;
import maquette.core.MaquetteRuntime;
import maquette.core.values.user.AuthenticatedUser;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.providers.datasets.Datasets;
import maquette.datashop.providers.datasets.commands.ListVersionsCommand;
import maquette.datashop.providers.datasets.records.Records;

import java.util.concurrent.ExecutionException;

public class DatasetStepDefinitions extends DataAssetStepDefinitions {

    public DatasetStepDefinitions(MaquetteRuntime runtime) {
        super(runtime, Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(), null);
    }

    public void $_uploads_records_$_to_dataset_$(AuthenticatedUser user, Records records, String dataset) throws ExecutionException, InterruptedException {
        var datasets = this
            .runtime
            .getModule(MaquetteDataShop.class)
            .getProviders()
            .getByType(Datasets.class);

        var revision = datasets
            .getServices()
            .create(user, dataset, records.getSchema())
            .toCompletableFuture()
            .get();

        datasets
            .getServices()
            .upload(user, dataset, revision.getId(), records)
            .toCompletableFuture()
            .get();

        datasets
            .getServices()
            .commit(user, dataset, revision.getId(), "uploaded some sample records");

        mentionedAssets.add(dataset);
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

        System.out.println(output);
    }
}
