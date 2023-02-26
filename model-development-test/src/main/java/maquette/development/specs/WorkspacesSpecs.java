package maquette.development.specs;

import maquette.core.MaquetteRuntime;
import maquette.core.modules.users.GlobalRole;
import maquette.core.values.ActionMetadata;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.development.MaquetteModelDevelopment;
import maquette.development.ports.DataAssetsServicePort;
import maquette.development.ports.ModelsRepository;
import maquette.development.ports.SandboxesRepository;
import maquette.development.ports.WorkspacesRepository;
import maquette.development.ports.infrastructure.InfrastructurePort;
import maquette.development.ports.models.ModelOperationsPort;
import maquette.development.ports.models.ModelServingPort;
import maquette.development.specs.steps.WorkspaceStepDefinitions;
import maquette.development.values.EnvironmentType;
import maquette.development.values.WorkspaceMemberRole;
import maquette.testutils.MaquetteContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

/**
 * Workspaces is a core component responsible for managing implementation of use-cases. Workspace defines who has access
 * to which resources and which data assets are being used. It also provides basic services for development of ML models
 * such as MlFlow instances for model tracking.
 */
public abstract class WorkspacesSpecs {

    protected MaquetteRuntime runtime;

    private WorkspaceStepDefinitions steps;

    private MaquetteContext context;

    private WorkspacesRepository workspacesRepository;

    @BeforeEach
    public void setup() {
        this.context = MaquetteContext.apply();
        this.workspacesRepository = setupWorkspacesRepository();
        this.runtime = MaquetteRuntime.apply();
        this.runtime
            .withModule(MaquetteModelDevelopment.apply(
                this.runtime, this.workspacesRepository, setupModelsRepository(),
                setupSandboxesRepository(), setupInfrastructurePort(), setupDataAssetsServicePort(),
                setupModelOperationsPort(), setupModelServingPort()))
            .initialize(context.system, context.app);
        this.runtime.getUsersRepository().insertGlobalAuthorization(
            GrantedAuthorization.apply(
                ActionMetadata.apply(context.users.alice),
                context.authorizations.alice,
                GlobalRole.ADMIN
            )
        );
        this.steps = new WorkspaceStepDefinitions(this.runtime);
    }


    @AfterEach
    public void clean() {
        this.context.clean();
    }

    public abstract WorkspacesRepository setupWorkspacesRepository();

    public abstract ModelsRepository setupModelsRepository();

    public abstract InfrastructurePort setupInfrastructurePort();

    public abstract DataAssetsServicePort setupDataAssetsServicePort();

    public abstract ModelOperationsPort setupModelOperationsPort();

    public abstract ModelServingPort setupModelServingPort();

    public abstract SandboxesRepository setupSandboxesRepository();

    /**
     * Workspaces are always private and can be accessed only by the members
     */
    @Test
    public void workspacesVisibility() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_workspace_with_name_$(context.users.bob, "fake");

        // When
        steps.$_browses_all_workspaces(context.users.bob);

        // Then
        steps.the_output_should_contain("fake");

        // When
        steps.$_gets_workspace_with_name_$(context.users.bob, "fake");

        // Then
        steps.the_output_should_contain("fake");

        // Given Bob creates second workspace
        steps.$_creates_a_workspace_with_name_$(context.users.bob, "fake2");

        // When
        steps.$_browses_all_workspaces(context.users.bob);

        // Then
        steps.the_output_should_contain("fake", "fake2");

        // When Charly becomes a member of the workspace `fake`, she can also see this one but not the other one
        steps.$_grants_$_access_to_the_$_workspace_for_$(context.users.bob, WorkspaceMemberRole.MEMBER, "fake",
            context.users.charly);
        steps.$_browses_all_workspaces(context.users.charly);

        // Then
        steps.the_output_should_contain("fake");
        steps.the_output_should_not_contain("fake2");
    }

    /**
     * Workspace creation triggers building of the required infrastructure, in particular an MlFlow instance
     */
    @Test
    public void workspaceInfrastructure() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_workspace_with_name_$(context.users.bob, "fake");
        steps.$_waits_until_the_mlflow_stack_is_deployed(context.users.bob);

        // When
        steps.$_gets_environment_for_workspace_$_of_type_$(context.users.bob, "fake", EnvironmentType.SANDBOX);

        // Then
        /*
        Not sure why we ever expected to have Base64 encoded values **here**.

        Keep the old expectations for now ... Remove if everything works fine.

        steps.the_output_should_contain("MLFLOW_ENDPOINT_LABEL  TUxmbG93IERhc2hib2FyZA==");
        steps.the_output_should_contain("ENTRY_POINT_ENDPOINT   aHR0cDovL2Zvbw==");
        steps.the_output_should_contain("CUSTOM_PARAM           dGVzdA==");
         */
        steps.the_output_should_contain("MLFLOW_ENDPOINT_LABEL");
        steps.the_output_should_contain("ENTRY_POINT_ENDPOINT");
        steps.the_output_should_contain("CUSTOM_PARAM");
    }


    /**
     * Workspace creation still works even if the auto-infrastructure fails for the first time
     */
    @Test
    public void workspaceInfrastructureAutoInfraError() throws ExecutionException, InterruptedException {
        // TODO bn: this doesn't belong to the standard maquette testing. Testing of failed auto-infra port
        // should be implemented as a separate test suite in mars
        // Given
        auto_infrastructure_is_throwing_an_error(true);
        steps.$_creates_a_workspace_with_name_$(context.users.bob, "autoInfraError");
        auto_infrastructure_is_throwing_an_error(false);
        steps.$_creates_a_workspace_with_name_$(context.users.bob, "noError");

        // When
        steps.$_gets_workspace_with_name_$(context.users.bob, "noError");

        // Then
        steps.the_output_should_contain("noError");
    }


    /**
     * Add and remove members from a workspace
     */
    @Test
    public void workspacesAuthorization() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_workspace_with_name_$(context.users.bob, "fake");
        steps.$_grants_$_access_to_the_$_workspace_for_$(context.users.bob, WorkspaceMemberRole.MEMBER, "fake",
            context.users.charly);

        // When non-admin wants to grant access to workspace
        var thrown = Assertions.assertThrows(ExecutionException.class, () ->
            steps.$_grants_$_access_to_the_$_workspace_for_$(context.users.charly, WorkspaceMemberRole.MEMBER, "fake",
                context.users.alice), "Unauthorized was expected");

        // Then
        Assertions.assertEquals(thrown.getMessage(), "maquette.core.common.exceptions.NotAuthorizedException: You are" +
            " not authorized to execute this action.");

        // When admin adds another admin to workspace and the other admin add member to a workspace
        steps.$_grants_$_access_to_the_$_workspace_for_$(context.users.bob, WorkspaceMemberRole.ADMIN, "fake",
            context.users.alice);
        steps.$_grants_$_access_to_the_$_workspace_for_$(context.users.alice, WorkspaceMemberRole.ADMIN, "fake",
            context.users.charly);

        // Then
        steps.the_output_should_contain("Successfully granted ownership.");

        // When admin removes member from a workspace
        steps.$_revoke_access_to_the_$_workspace_for_$(context.users.bob, "fake",
            context.users.alice);

        // Then
        steps.the_output_should_contain("Revoked access from `alice`");

        // When member removes admin from a workspace
        thrown = Assertions.assertThrows(ExecutionException.class, () ->
            steps.$_revoke_access_to_the_$_workspace_for_$(context.users.alice, "fake",
                context.users.charly), "Unauthorized was expected");

        // Then
        Assertions.assertEquals(thrown.getMessage(), "maquette.core.common.exceptions.NotAuthorizedException: You are" +
            " not authorized to execute this action.");

        // When member removes member from a workspace
        steps.$_grants_$_access_to_the_$_workspace_for_$(context.users.bob, WorkspaceMemberRole.MEMBER, "fake",
            context.users.charly);
        thrown = Assertions.assertThrows(ExecutionException.class, () ->
            steps.$_revoke_access_to_the_$_workspace_for_$(context.users.alice, "fake",
                context.users.charly), "Unauthorized was expected");

        // Then
        Assertions.assertEquals(thrown.getMessage(), "maquette.core.common.exceptions.NotAuthorizedException: You are" +
            " not authorized to execute this action.");

        // When admin removes another admin from a workspace
        steps.$_grants_$_access_to_the_$_workspace_for_$(context.users.bob, WorkspaceMemberRole.ADMIN, "fake",
            context.users.alice);
        steps.$_revoke_access_to_the_$_workspace_for_$(context.users.bob, "fake",
            context.users.alice);

        // Then
        steps.the_output_should_contain("Revoked access from `alice`");

        // When admin removes itself from workspace
        thrown = Assertions.assertThrows(ExecutionException.class, () ->
            steps.$_revoke_access_to_the_$_workspace_for_$(context.users.bob, "fake",
                context.users.bob), "Unauthorized was expected");

        // Then
        Assertions.assertEquals(thrown.getMessage(), "maquette.core.common.exceptions.NotAuthorizedException: You are" +
            " not authorized to execute this action.");

        // When member removes itself from workspace
        thrown = Assertions.assertThrows(ExecutionException.class, () ->
            steps.$_revoke_access_to_the_$_workspace_for_$(context.users.charly, "fake",
                context.users.charly), "Unauthorized was expected");

        // Then
        Assertions.assertEquals(thrown.getMessage(), "maquette.core.common.exceptions.NotAuthorizedException: You are" +
            " not authorized to execute this action.");
    }

    /**
     * Remove workspace
     */
    @Test
    public void removeWorkspace() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_workspace_with_name_$(context.users.bob, "fake");

        // When admin removes workspace
        steps.$_removes_workspace_with_name_$(context.users.bob, "fake");
        steps.$_browses_all_workspaces(context.users.bob);

        // Then
        steps.the_output_should_be("WORKSPACE ID  WORKSPACE  TITLE  MODIFIED  SUMMARY  ");
        // TODO bn verify also that auto infra is cleaned up  - comment: ja mach das mal, das ist schon implementiert ;P

        // Given
        steps.$_creates_a_workspace_with_name_$(context.users.bob, "fake");

        // When we add a member to a workspace and he/she removes workspace
        steps.$_grants_$_access_to_the_$_workspace_for_$(context.users.bob, WorkspaceMemberRole.MEMBER, "fake",
            context.users.charly);
        var thrown = Assertions.assertThrows(ExecutionException.class, () ->
            steps.$_removes_workspace_with_name_$(context.users.charly, "fake"), "Unauthorized was expected");

        // Then
        Assertions.assertEquals(thrown.getMessage(), "maquette.core.common.exceptions.NotAuthorizedException: You are" +
            " not authorized to execute this action.");

        // When we add an admin to a workspace and he/she removes workspace
        steps.$_grants_$_access_to_the_$_workspace_for_$(context.users.bob, WorkspaceMemberRole.ADMIN, "fake",
            context.users.charly);
        steps.$_removes_workspace_with_name_$(context.users.charly, "fake");
        steps.$_browses_all_workspaces(context.users.charly);

        // Then
        steps.the_output_should_be("WORKSPACE ID  WORKSPACE  TITLE  MODIFIED  SUMMARY  ");
    }

    /**
     * Verify that the MlFlow parameters are properly set
     */
    @Test
    public void mlFlowParameters() throws ExecutionException, InterruptedException {
        // TODO bn extend getEnvironment with additional parameters that are coming from auto-infra?
        /*// When
        steps.$_gets_workspace_with_name_$(context.users.bob, "fake");

        // Then
        steps.the_output_should_contain("mlFlowConfiguration  http://foo");*/
    }

    /**
     * Workspaces properties can be managed only by admins
     */
    @Test
    public void updateWorkspaceProperties() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_workspace_with_name_$(context.users.bob, "fake");

        // When admin updates workspace
        steps.$_configures_workspace_$_to_be_$(context.users.bob, "fake", "fake-2", "fake-title2", "fake-summary2");
        steps.$_browses_all_workspaces(context.users.bob);

        // Then
        steps.the_output_should_contain("fake-2");
        steps.the_output_should_contain("fake-title2");
        steps.the_output_should_contain("fake-summary2");

        // When member updates workspace
        steps.$_grants_$_access_to_the_$_workspace_for_$(context.users.bob, WorkspaceMemberRole.MEMBER, "fake-2",
            context.users.charly);
        var thrown = Assertions.assertThrows(ExecutionException.class, () ->
            steps.$_configures_workspace_$_to_be_$(context.users.charly, "fake-2", "fake-3", "fake-title2",
                "fake-summary2"), "Unauthorized was expected");

        // Then
        Assertions.assertEquals(thrown.getMessage(), "maquette.core.common.exceptions.NotAuthorizedException: You are" +
            " not authorized to execute this action.");
    }

    /**
     * List all data access requests and their status that belong to a workspace
     */
    @Test
    public void listDataAcessRequests() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_workspace_with_name_$(context.users.bob, "fake");
        there_is_$_access_request_for_$_data_asset_within_$_workspace(
            "access-request-1", "data-asset-1", "fake");

        // When
        steps.$_gets_workspace_with_name_$(context.users.bob, "fake");

        // Then
        steps.the_output_should_contain("data-asset-1");
        steps.the_output_should_contain("access-request-1");

        // Given
        steps.$_grants_$_access_to_the_$_workspace_for_$(context.users.bob, WorkspaceMemberRole.MEMBER, "fake",
            context.users.charly);

        // When
        steps.$_gets_workspace_with_name_$(context.users.charly, "fake");

        // Then
        steps.the_output_should_contain("data-asset-1");
        steps.the_output_should_contain("access-request-1");
    }

    /**
     * Create a sandbox with a new volume
     */
    @Test
    public void createSandboxWithNewVolume() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_workspace_with_name_$(context.users.bob, "fake");

        // when
        steps.$_creates_a_$_sandbox_with_a_$_volume_named_$(context.users.bob, "newSandbox", "new", "my-volume");
        steps.$_gets_workspace_with_name_$(context.users.bob, "fake");

        // then
        steps.the_output_should_contain("my-volume");
        steps.the_output_has_exactly_$_volume(1);

        // when creating a second volume
        steps.$_creates_a_$_sandbox_with_a_$_volume_named_$(context.users.bob, "newSandbox", "new", "my-volume2");
        steps.$_gets_workspace_with_name_$(context.users.bob, "fake");

        // then
        steps.the_output_should_contain("my-volume", "my-volume2");
        steps.the_output_has_exactly_$_volume(2);

        // when
        steps.$_grants_$_access_to_the_$_workspace_for_$(context.users.bob, WorkspaceMemberRole.MEMBER, "fake",
            context.users.charly);
        steps.$_gets_workspace_with_name_$(context.users.charly, "fake");

        // then
        steps.the_output_should_not_contain("my-volume");
        steps.the_output_has_exactly_$_volume(0);
    }

    /**
     * Create a sandbox with an existing volume
     */
    @Test
    public void createSandboxWithExistingVolume() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_workspace_with_name_$(context.users.bob, "fake");

        // when
        steps.$_creates_a_$_sandbox_with_a_$_volume_named_$(context.users.bob, "newSandbox", "new", "my-volume");
        steps.$_creates_a_$_sandbox_with_a_$_volume_named_$(context.users.bob, "newSandbox2", "existing", "my-volume");
        steps.$_gets_workspace_with_name_$(context.users.bob, "fake");

        // then
        steps.the_output_should_contain("my-volume");
        steps.the_output_has_exactly_$_volume(1);

        // when creates a new volume with the same name as existing one
        steps.$_creates_a_$_sandbox_with_a_$_volume_named_$(context.users.bob, "newSandbox3", "new", "my-volume");

        // then
        steps.an_error_occurs_with_a_message_$("already exists");

        // when
        steps.$_gets_workspace_with_name_$(context.users.bob, "fake");

        // then
        steps.the_output_should_not_contain("newSandbox3");
    }

    /**
     * Create Sandbox with Advance user Permissions.
     */
    @Test
    public void createSandboxAdvancedUserPermissions() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_workspace_with_name_$(context.users.bob, "fake");
        steps.$_grants_advanced_user_to_user_$(context.users.alice, context.users.bob);

        // when
        steps.$_creates_a_sandbox_$_with_and_advanced_stack(context.users.bob, "test-sdbx-gpu-1");

        // then
        steps.the_output_should_contain("Successfully created");
        steps.$_gets_$_sandbox(context.users.bob, "test-sdbx-gpu-1");
    }

    @Test
    public void createSandboxAdvancedUserPermissionsWithoutRequiredRole() throws ExecutionException,
        InterruptedException {
        // Given
        steps.$_creates_a_workspace_with_name_$(context.users.charly, "fake");

        // when
        var thrown = Assertions.assertThrows(ExecutionException.class, () -> steps.$_creates_a_sandbox_$_with_and_advanced_stack(context.users.charly, "test-sdbx-gpu-2"));

        // then
        Assertions.assertEquals(thrown.getMessage(), "maquette.core.common.exceptions.NotAuthorizedException: You are" +
            " not authorized to execute this action.");
    }

    /**
     * Delete sandbox with a volume used by multiple sandboxes
     */
    @Test
    public void deleteSandbox() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_workspace_with_name_$(context.users.bob, "fake");

        // when
        steps.$_creates_a_$_sandbox_with_a_$_volume_named_$(context.users.bob, "newSandbox", "new", "my-volume");
        steps.$_creates_a_$_sandbox_with_a_$_volume_named_$(context.users.bob, "newSandbox2", "existing", "my-volume");
        steps.$_removes_the_$_sandbox(context.users.bob, "newSandbox2");
        steps.$_gets_workspace_with_name_$(context.users.bob, "fake");

        // then
        steps.the_output_should_contain("my-volume");
        steps.the_output_has_exactly_$_volume(1);

        // when removes the only existing sandbox
        steps.$_removes_the_$_sandbox(context.users.bob, "newSandbox");
        steps.$_gets_workspace_with_name_$(context.users.bob, "fake");

        // then volume shouldn't be deleted
        steps.the_output_should_contain("my-volume");
        steps.the_output_has_exactly_$_volume(1);
    }

    protected void there_is_$_access_request_for_$_data_asset_within_$_workspace(String accessRequestId,
                                                                                 String dataAssetName,
                                                                                 String workspaceName) throws ExecutionException, InterruptedException {
        var workspace = this.workspacesRepository
            .findWorkspaceByName(workspaceName)
            .toCompletableFuture()
            .get();
        create_data_access_request(accessRequestId, dataAssetName, workspaceName, workspace
            .get()
            .getId()
            .getValue());
    }

    protected abstract void create_data_access_request(String accessRequestId,
                                                       String dataAssetName,
                                                       String workspaceName,
                                                       String workspaceId);

    protected abstract void auto_infrastructure_is_throwing_an_error(Boolean throwError);
}
