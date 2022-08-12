package maquette.datashop.specs;

import maquette.core.MaquetteRuntime;
import maquette.core.ports.email.FakeEmailClient;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.ports.FakeWorkspacesServicePort;
import maquette.datashop.providers.FakeProvider;
import maquette.datashop.providers.collections.Collections;
import maquette.datashop.providers.collections.ports.CollectionsRepositories;
import maquette.datashop.providers.collections.ports.CollectionsRepository;
import maquette.datashop.providers.datasets.ports.DatasetsRepository;
import maquette.datashop.specs.steps.CollectionStepDefinitions;
import maquette.testutils.MaquetteContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public abstract class CollectionSpecs {

    private CollectionStepDefinitions steps;

    private MaquetteContext context;

    @BeforeEach
    public void setup() {
        this.context = MaquetteContext.apply();

        var runtime = MaquetteRuntime.apply();
        var workspaces = FakeWorkspacesServicePort.apply();
        var collections = Collections.apply(setupCollectionsRepository(), workspaces, runtime);
        var shop = MaquetteDataShop.apply(setupDataAssetsRepository(), workspaces, FakeEmailClient.apply(),
            FakeProvider.apply(), collections);

        var maquette = runtime
            .withModule(shop)
            .initialize(context.system, context.app);

        var ressourcePath = getRessourcePath();

        this.steps = new CollectionStepDefinitions(maquette, workspaces, ressourcePath);
    }

    @AfterEach
    public void clean() {
        this.context.clean();

        // remove files
        var downloads = this.steps.getDownloaded();
        downloads.forEach((n) -> new File(n.toString()).delete());

    }

    public abstract CollectionsRepository setupCollectionsRepository();

    public abstract DataAssetsRepository setupDataAssetsRepository();

    public abstract Path getRessourcePath();

    /**
     * When uploading a single file to a collection and downloading the collection file after, the downloaded file should
     * be identical to the uploaded one.
     */
    @Test
    public void uploadAndDownloadFile() throws ExecutionException, InterruptedException, IOException {
        // Given
        steps.$_creates_a_data_asset_of_type_$_with_name_$(context.users.bob, "collection", "some-asset");

        // When
        steps.$_uploads_file_$_to_collection_$(context.users.bob, "some-asset", "test.txt");
        steps.$_downloads_file_$_from_collection_$(context.users.bob, "some-asset", "test.txt");

        // Then
        steps.the_uploaded_files_should_equal_the_downloaded_files();
    }


    /**
     * When uploading a directory to a collection, all files in the directory should be listed in the collection files.
     */
    @Test
    public void uploadZippedDirectory() throws ExecutionException, InterruptedException, IOException {
        // Given
        steps.$_creates_a_data_asset_of_type_$_with_name_$(context.users.bob, "collection", "some-asset");

        // When
        steps.$_uploads_dir_$_to_collection_$(context.users.bob, "some-asset", "test_dir.zip");

        // Then
        steps.the_uploaded_files_should_be_listed_in_collection_files(
                context.users.bob,
                "some-asset",
                "",
                Arrays.asList("test_dir/other_test.txt", "test_dir/test.txt")
        );
    }


    /**
     * When uploading a file to the collection, then creating a tag for the collection, and finally adding another file
     * to the collection, only the files uploaded before the tag should be listed in the collection files with the
     * given tag.
     */
    @Test
    public void collectionTagging() throws ExecutionException, InterruptedException, IOException {
        // Given
        steps.$_creates_a_data_asset_of_type_$_with_name_$(context.users.bob, "collection", "some-asset");

        // When
        steps.$_uploads_file_$_to_collection_$(context.users.bob, "some-asset", "test.txt");
        steps.$_creates_tag_$_for_collection_$(context.users.bob, "some-asset", "test_tag");
        steps.$_uploads_file_$_to_collection_$(context.users.bob, "some-asset", "other_test.txt");

        // Then
        steps.the_uploaded_files_should_be_listed_in_collection_files(
                context.users.bob,
                "some-asset",
                "test_tag",
                Arrays.asList("test.txt")
        );
    }


    /**
     * When uploading a file to a collection and deleting it again, the file should not be listed in the
     * collection files anymore.
     */
    @Test
    public void deleteFile() throws ExecutionException, InterruptedException, IOException {
        // Given
        steps.$_creates_a_data_asset_of_type_$_with_name_$(context.users.bob, "collection", "some-asset");

        // When
        steps.$_uploads_file_$_to_collection_$(context.users.bob, "some-asset", "test.txt");
        steps.$_deletes_file_$_from_collection_$(context.users.bob, "some-asset", "test.txt");

        // Then
        steps.the_deleted_files_should_not_be_listed_in_collection_files(
                context.users.bob,
                "some-asset",
                Arrays.asList("test.txt")
        );
    }
}
