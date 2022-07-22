package maquette.datashop.specs.steps;

import com.google.common.collect.Lists;
import maquette.core.MaquetteRuntime;
import maquette.core.values.binary.BinaryObjects;
import maquette.core.values.user.AuthenticatedUser;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.ports.FakeWorkspacesServicePort;
import maquette.datashop.providers.collections.Collections;
import maquette.datashop.providers.datasets.Datasets;
import maquette.datashop.providers.datasets.records.Records;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectionStepDefinitions extends DataAssetStepDefinitions {

    private final List<Path> uploaded;


    private final List<Path> downloaded;


    public List<Path> getDownloaded() {
        return downloaded;
    }

    public CollectionStepDefinitions(MaquetteRuntime runtime, FakeWorkspacesServicePort workspaces) {
        super(runtime, workspaces, Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(), null);
        this.uploaded = Lists.newArrayList();
        this.downloaded = Lists.newArrayList();
    }

    public void $_uploads_file_$_to_collection_$(AuthenticatedUser user, String collection) throws ExecutionException, InterruptedException {
        var collections = this
                .runtime
                .getModule(MaquetteDataShop.class)
                .getProviders()
                .getByType(Collections.class);

        var file = Paths.get("./src/main/ressources/test.txt");
        var bin = BinaryObjects.fromFile(file);

        collections
                .getServices()
                .put(user, collection, bin, "test.txt", "upload some test file")
                .toCompletableFuture()
                .get();

        uploaded.add(file);
    }

    public void $_downloads_file_$_from_collection_$(AuthenticatedUser user, String collection) throws ExecutionException, InterruptedException {
        var collections = this
                .runtime
                .getModule(MaquetteDataShop.class)
                .getProviders()
                .getByType(Collections.class);

        var file = Paths.get("./src/main/ressources/download_test.txt");

        collections
                .getServices()
                .read(user, collection, "test.txt")
                .toCompletableFuture()
                .get()
                .toFile(file);

        downloaded.add(file);
    }

    public void the_uploaded_files_should_equal_the_downloaded_files() throws IOException {
        var uploaded = new File(this.uploaded.get(this.uploaded.size() -  1).toString());
        var downloaded = new File(this.downloaded.get(this.downloaded.size() - 1).toString());

        assert(FileUtils.contentEquals(uploaded, downloaded));
    }
}
