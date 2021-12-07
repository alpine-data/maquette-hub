package maquette.datashop.specs.steps;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.commands.CreateDataAssetCommand;
import maquette.datashop.commands.ListDataAssetsCommand;
import maquette.datashop.commands.UpdateDataAssetCommand;
import maquette.datashop.commands.members.GrantDataAssetMemberCommand;
import maquette.datashop.commands.requests.ApproveAccessRequestCommand;
import maquette.datashop.commands.requests.CreateAccessRequestCommand;
import maquette.datashop.commands.requests.GrantAccessRequestCommand;
import maquette.datashop.commands.requests.ListAccessRequestsCommand;
import maquette.datashop.ports.FakeWorkspacesServicePort;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.access_requests.DataAccessRequestProperties;
import maquette.datashop.values.metadata.DataClassification;
import maquette.datashop.values.metadata.DataVisibility;
import maquette.datashop.values.metadata.DataZone;
import maquette.datashop.values.metadata.PersonalInformation;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@AllArgsConstructor()
public class DataAssetStepDefinitions {

    protected final MaquetteRuntime runtime;

    protected final FakeWorkspacesServicePort workspaces;

    protected final List<String> mentionedAssets;

    protected final List<String> results;

    protected List<DataAccessRequestProperties> knownAccessRequests;

    protected DataAccessRequestProperties mentionedAccessRequest;

    public DataAssetStepDefinitions(MaquetteRuntime runtime, FakeWorkspacesServicePort workspaces) {
        this(runtime, workspaces, Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(), null);
    }

    public void $_browses_all_data_assets(AuthenticatedUser user) throws ExecutionException, InterruptedException {
        var result = ListDataAssetsCommand
            .apply(null)
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void $_configures_data_asset_$_to_be_$(AuthenticatedUser user, String name, DataVisibility visibility) throws ExecutionException, InterruptedException {
        var asset = runtime.getModule(MaquetteDataShop.class).getServices().get(user, name).toCompletableFuture().get();
        var update = UpdateDataAssetCommand
            .apply(
                name,
                asset.getProperties().getMetadata().withVisibility(visibility))
            .run(user, runtime)
            .toCompletableFuture()
            .get();

        mentionedAssets.add(name);
    }

    public void $_creates_a_data_asset_of_type_$_with_name_$(AuthenticatedUser user, String type, String name) throws ExecutionException, InterruptedException {
        var result = CreateDataAssetCommand
            .apply(
                type,
                name,
                name,
                "Some nice speaking summary.",
                DataVisibility.PUBLIC,
                DataClassification.PUBLIC,
                PersonalInformation.NONE,
                DataZone.RAW,
                user.getId().getValue(),
                null,
                null,
                null)
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        mentionedAssets.add(name);
        results.add(result);
    }

    public void $_creates_a_data_asset_with_sensitive_data_of_type_$_with_name_$(AuthenticatedUser user,
                                                                                 String dataAssetType,
                                                                                 String dataAssetName) throws ExecutionException, InterruptedException {
        var result = CreateDataAssetCommand
            .apply(
                dataAssetType,
                dataAssetName,
                dataAssetName,
                "Some nice speaking summary.",
                DataVisibility.PUBLIC,
                DataClassification.CONFIDENTIAL,
                PersonalInformation.SENSITIVE_PERSONAL_INFORMATION,
                DataZone.RAW,
                user.getId().getValue(),
                null,
                null,
                null)
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        mentionedAssets.add(dataAssetName);
        results.add(result);
    }

    public void $_lists_access_requests_for_asset_$(User user, String asset) throws ExecutionException,
        InterruptedException {

        var result = ListAccessRequestsCommand
            .apply(asset)
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        this.knownAccessRequests = runtime
            .getModule(MaquetteDataShop.class)
            .getServices()
            .getDataAccessRequests(user, asset)
            .toCompletableFuture()
            .get();

        mentionedAssets.add(asset);
        results.add(result);
    }

    public void $_approves_the_access_request(AuthenticatedUser user) throws ExecutionException, InterruptedException {
        var result = ApproveAccessRequestCommand
            .apply(
                mentionedAssets.get(mentionedAssets.size() - 1),
                mentionedAccessRequest.getId(),
                "It's fine")
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void $_grants_this_access_request(AuthenticatedUser bob) throws ExecutionException, InterruptedException {
        var result = GrantAccessRequestCommand
            .apply(
                mentionedAssets.get(mentionedAssets.size() - 1),
                mentionedAccessRequest.getId(),
                null,
                "It's ok",
                null,
                false)
            .run(bob, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void $_grants_consumer_access_rights_for_$(User executor, String assetName, AuthenticatedUser grantedUser) throws ExecutionException, InterruptedException {
        var result = GrantDataAssetMemberCommand
            .apply(assetName, grantedUser.toAuthorization()
                .toGenericAuthorizationDefinition(), DataAssetMemberRole.CONSUMER)
            .run(executor, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        mentionedAssets.add(assetName);
        results.add(result);
    }

    public void $_is_data_owner_of_data_asset_$(AuthenticatedUser user, String dataAssetName) throws ExecutionException, InterruptedException {
        runtime
            .getModule(MaquetteDataShop.class)
            .getEntities()
            .getByName(dataAssetName)
            .toCompletableFuture()
            .get()
            .getMembers()
            .addMember(user, UserAuthorization.apply(user.getId().getValue()), DataAssetMemberRole.OWNER)
            .toCompletableFuture()
            .get();
    }

    public void $_requests_access_for_asset_$_on_behalf_of_$(User user, String asset, String workspace) throws ExecutionException,
        InterruptedException {

        $_requests_access_for_asset_$_on_behalf_of_$_with_reason_$(user, asset, workspace, "some very good reason");
    }

    public void $_requests_access_for_asset_$_on_behalf_of_$_with_reason_$(User user, String asset, String workspace,
                                                                           String reason) throws ExecutionException,
        InterruptedException {

        var result = CreateAccessRequestCommand
            .apply(asset, workspace, reason)
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        mentionedAssets.add(asset);
        results.add(result);
    }

    public void $_should_be_able_to_read_asset_$(AuthenticatedUser user, String asset) throws ExecutionException,
        InterruptedException {

        var canRead = runtime
            .getModule(MaquetteDataShop.class)
            .getServices()
            .get(user, asset)
            .toCompletableFuture()
            .get()
            .getDataAssetPermissions(user)
            .canConsume();

        assertThat(canRead).isTrue();
    }

    public void $_should_not_be_able_to_read_asset_$(AuthenticatedUser user, String asset) throws ExecutionException,
        InterruptedException {

        var canRead = runtime
            .getModule(MaquetteDataShop.class)
            .getServices()
            .get(user, asset)
            .toCompletableFuture()
            .get()
            .getDataAssetPermissions(user)
            .canConsume();

        assertThat(canRead).isFalse();
    }

    public void the_output_should_contain(String... queries) {
        var result = results.get(results.size() - 1);

        for (var q : queries) {
            assertThat(result).contains(q);
        }
    }

    public void the_output_should_contain_the_access_request_of(AuthenticatedUser user) {
        var result = knownAccessRequests
            .stream()
            .filter(properties -> properties.getCreated().getBy().equals(user.getId().getValue()))
            .findFirst();

        assertThat(result).isPresent();
        this.mentionedAccessRequest = result.get();
    }

    public void the_output_should_not_contain(String... queries) {
        var result = results.get(results.size() - 1);

        for (var q : queries) {
            assertThat(result).doesNotContain(q);
        }
    }

    public void $_is_member_of_workspace_$(AuthenticatedUser user, String workspaceName) {
        this.workspaces.createWorkspaceIfNotPresent(user, workspaceName);
    }

}
