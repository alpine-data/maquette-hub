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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectionStepDefinitions extends DataAssetStepDefinitions {

    private final List<Path> uploaded;


    private final List<Path> downloaded;


    private final Path ressourcePath;


    public List<Path> getDownloaded() {
        return downloaded;
    }

    public CollectionStepDefinitions(MaquetteRuntime runtime, FakeWorkspacesServicePort workspaces, Path ressourcePath) {
        super(runtime, workspaces, Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(), null);
        this.uploaded = Lists.newArrayList();
        this.downloaded = Lists.newArrayList();
        this.ressourcePath = ressourcePath;
    }

    public void $_uploads_file_$_to_collection_$(AuthenticatedUser user, String collection, String fileName) throws ExecutionException, InterruptedException {
        var collections = this
                .runtime
                .getModule(MaquetteDataShop.class)
                .getProviders()
                .getByType(Collections.class);

        var file = Paths.get(String.format(this.ressourcePath + "/" + fileName));
        var bin = BinaryObjects.fromFile(file);

        collections
                .getServices()
                .put(user, collection, bin, fileName, "upload some test file")
                .toCompletableFuture()
                .get();

        uploaded.add(file);
    }

    public void $_downloads_file_$_from_collection_$(AuthenticatedUser user, String collection, String fileName) throws ExecutionException, InterruptedException {
        var collections = this
                .runtime
                .getModule(MaquetteDataShop.class)
                .getProviders()
                .getByType(Collections.class);

        var file = Paths.get(this.ressourcePath + "/download_test.txt");

        collections
                .getServices()
                .read(user, collection, fileName)
                .toCompletableFuture()
                .get()
                .toFile(file);

        downloaded.add(file);
    }

    public void $_uploads_dir_$_to_collection_$(AuthenticatedUser user, String collection, String zipName) throws ExecutionException, InterruptedException {
        var collections = this
                .runtime
                .getModule(MaquetteDataShop.class)
                .getProviders()
                .getByType(Collections.class);

        var file = Paths.get(String.format(this.ressourcePath + "/" + zipName));
        var bin = BinaryObjects.fromFile(file);

        collections
                .getServices()
                .putAll(user, collection, bin, "", "upload some test file")
                .toCompletableFuture()
                .get();

        uploaded.add(file);
    }

    public void $_creates_tag_$_for_collection_$(AuthenticatedUser user, String collection, String tagName) throws ExecutionException, InterruptedException {
        var collections = this
                .runtime
                .getModule(MaquetteDataShop.class)
                .getProviders()
                .getByType(Collections.class);

        collections
                .getServices()
                .tag(user, collection, tagName, "")
                .toCompletableFuture()
                .get();
    }

    public void $_deletes_file_$_from_collection_$(AuthenticatedUser user, String collection, String fileName) throws ExecutionException, InterruptedException {
        var collections = this
                .runtime
                .getModule(MaquetteDataShop.class)
                .getProviders()
                .getByType(Collections.class);

        collections
                .getServices()
                .remove(user, collection, fileName)
                .toCompletableFuture()
                .get();
    }

    public void the_uploaded_files_should_equal_the_downloaded_files() throws IOException {
        var uploaded = new File(this.uploaded.get(this.uploaded.size() -  1).toString());
        var downloaded = new File(this.downloaded.get(this.downloaded.size() - 1).toString());

        assert(FileUtils.contentEquals(uploaded, downloaded));
    }

    public void the_uploaded_files_should_be_listed_in_collection_files(AuthenticatedUser user, String collection, String tag, List<String> uploadedFiles) throws ExecutionException, InterruptedException {
        var collections = this
                .runtime
                .getModule(MaquetteDataShop.class)
                .getProviders()
                .getByType(Collections.class);

        List<String> files;
        if (tag == "") {
            files = collections
                    .getServices()
                    .listFiles(user, collection)
                    .toCompletableFuture()
                    .get();
        } else {
            files = collections
                    .getServices()
                    .listFiles(user, collection, tag)
                    .toCompletableFuture()
                    .get();
        }

        assert(files.equals(uploadedFiles));
    }

    public void the_deleted_files_should_not_be_listed_in_collection_files(AuthenticatedUser user, String collection, List<String> deletedFiles) throws ExecutionException, InterruptedException {
        var collections = this
                .runtime
                .getModule(MaquetteDataShop.class)
                .getProviders()
                .getByType(Collections.class);

        List<String> files;

        files = collections
                .getServices()
                .listFiles(user, collection)
                .toCompletableFuture()
                .get();

        Set<String> intersection = files.stream()
                .distinct()
                .filter(deletedFiles::contains)
                .collect(Collectors.toSet());

        assert(intersection.isEmpty());
    }
}
