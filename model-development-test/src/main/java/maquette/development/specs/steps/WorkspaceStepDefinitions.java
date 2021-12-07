package maquette.development.specs.steps;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import maquette.development.commands.CreateWorkspaceCommand;
import maquette.development.commands.ListWorkspacesCommand;
import maquette.development.commands.UpdateWorkspaceCommand;
import maquette.development.commands.members.GrantWorkspaceMemberCommand;
import maquette.development.values.WorkspaceMemberRole;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@AllArgsConstructor()
public class WorkspaceStepDefinitions {

    protected final MaquetteRuntime runtime;

    protected final List<String> mentionedWorkspaces;

    protected final List<String> results;

    public WorkspaceStepDefinitions(MaquetteRuntime runtime) {
        this(runtime, Lists.newArrayList(), Lists.newArrayList());
    }

    public void $_browses_all_workspaces(AuthenticatedUser user) throws ExecutionException, InterruptedException {
        var result = ListWorkspacesCommand
            .apply()
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void $_creates_a_workspace_with_name_$(AuthenticatedUser user,
                                                  String workspaceName) throws ExecutionException,
        InterruptedException {
        var result = CreateWorkspaceCommand
            .apply(workspaceName, "fake-title", "fake-summary")
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void $_updates_a_workspace_with_name_$_to_a_new_name_$(AuthenticatedUser user,
                                                                  String workspaceName,
                                                                  String newWorkspaceName) throws ExecutionException,
        InterruptedException {
        var result = UpdateWorkspaceCommand
            .apply(workspaceName, newWorkspaceName, "fake-title", "fake-summary")
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void $_gets_workspace_with_name_$(AuthenticatedUser user,
                                             String fake) {
    }

    public void $_grants_$_access_to_the_$_workspace_for_$(User executor,
                                                           WorkspaceMemberRole memberRole,
                                                           String workspaceName,
                                                           AuthenticatedUser grantedUser) throws ExecutionException,
        InterruptedException {
        var result = GrantWorkspaceMemberCommand
            .apply(workspaceName, grantedUser.toAuthorization().toGenericAuthorizationDefinition(), memberRole)
            .run(executor, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void the_output_should_contain(String... queries) {
        var result = results.get(results.size() - 1);

        for (var q : queries) {
            assertThat(result).contains(q);
        }
    }

    public void the_output_should_not_contain(String... queries) {
        var result = results.get(results.size() - 1);

        for (var q : queries) {
            assertThat(result).doesNotContain(q);
        }
    }


    /*public void $_browses_all_data_assets(AuthenticatedUser user) throws ExecutionException, InterruptedException {
        var result = ListDataAssetsCommand
            .apply()
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        results.add(result);
    }

    public void $_configures_data_asset_$_to_be_$(AuthenticatedUser user, String name, DataVisibility visibility)
    throws ExecutionException, InterruptedException {
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

    public void $_creates_a_data_asset_of_type_$_with_name_$(AuthenticatedUser user, String type, String name) throws
     ExecutionException, InterruptedException {
        var result = CreateDataAssetCommand
            .apply(
                type,
                name,
                name,
                "Some nice speaking summary.",
                DataVisibility.PUBLIC,
                DataClassification.RESTRICTED,
                PersonalInformation.PERSONAL_INFORMATION,
                DataZone.PREPARED,
                user.getId().getValue(),
                null,
                null)
            .run(user, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        mentionedAssets.add(name);
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

    public void $_grants_consumer_access_rights_for_$(User executor, String assetName, AuthenticatedUser grantedUser)
     throws ExecutionException, InterruptedException {
        var result = GrantDataAssetMemberCommand
            .apply(assetName, grantedUser.toAuthorization().toGenericAuthorizationDefinition(), DataAssetMemberRole
            .CONSUMER)
            .run(executor, runtime)
            .toCompletableFuture()
            .get()
            .toPlainText(runtime);

        mentionedAssets.add(assetName);
        results.add(result);
    }

    public void $_requests_access_for_asset_$(User user, String asset) throws ExecutionException,
        InterruptedException {

        $_requests_access_for_asset_$(user, asset, "some very good reason");
    }

    public void $_requests_access_for_asset_$(User user, String asset, String reason) throws ExecutionException,
        InterruptedException {

        var result = CreateAccessRequestCommand
            .apply(asset, "some-project", reason)
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

    public void the_output_should_contain_the_access_request_of(AuthenticatedUser user) {
        var result = knownAccessRequests
            .stream()
            .filter(properties -> properties.getCreated().getBy().equals(user.getId().getValue()))
            .findFirst();

        assertThat(result).isPresent();
        this.mentionedAccessRequest = result.get();
    }
    */
}
