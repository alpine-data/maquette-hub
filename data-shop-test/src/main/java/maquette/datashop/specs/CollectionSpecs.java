package maquette.datashop.specs;

import maquette.core.MaquetteRuntime;
import maquette.core.ports.email.FakeEmailClient;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.ports.FakeWorkspacesServicePort;
import maquette.datashop.providers.FakeProvider;
import maquette.datashop.providers.collections.Collections;
import maquette.datashop.providers.collections.ports.CollectionsRepositories;
import maquette.datashop.specs.steps.CollectionStepDefinitions;
import maquette.testutils.MaquetteContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public abstract class CollectionSpecs {

    private CollectionStepDefinitions steps;

    private MaquetteContext context;

    @BeforeEach
    public void setup() {
        this.context = MaquetteContext.apply();

        var runtime = MaquetteRuntime.apply();
        var workspaces = FakeWorkspacesServicePort.apply();
        var collections = Collections.apply(CollectionsRepositories.create(runtime.getObjectMapperFactory().createJsonMapper()), workspaces, runtime);
        var shop = MaquetteDataShop.apply(setupDataAssetsRepository(), workspaces, FakeEmailClient.apply(),
            FakeProvider.apply(), collections);

        var maquette = runtime
            .withModule(shop)
            .initialize(context.system, context.app);

        this.steps = new CollectionStepDefinitions(maquette, workspaces);
    }

    @AfterEach
    public void clean() {
        this.context.clean();

        // remove files
        var downloads = this.steps.getDownloaded();
        downloads.forEach((n) -> new File(n.toString()).delete());

    }


    /**
     * When uploading a single file to a collection and downloading the collection after, the downloaded file should
     * be identical to the uploaded one.
     */
    @Test
    public void uploadAndDownloadFiles() throws ExecutionException, InterruptedException, IOException {
        // Given
        steps.$_creates_a_data_asset_of_type_$_with_name_$(context.users.bob, "collection", "some-asset");

        // When
        steps.$_uploads_file_$_to_collection_$(context.users.bob, "some-asset");
        steps.$_downloads_file_$_from_collection_$(context.users.bob, "some-asset");

        // Then
        steps.the_uploaded_files_should_equal_the_downloaded_files();
    }

    public abstract DataAssetsRepository setupDataAssetsRepository();

}
