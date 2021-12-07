package maquette.development.specs;

import maquette.core.MaquetteRuntime;
import maquette.development.MaquetteModelDevelopment;
import maquette.development.ports.FakeDataAssetsServicePort;
import maquette.development.ports.InfrastructurePort;
import maquette.development.ports.ModelsRepository;
import maquette.development.ports.WorkspacesRepository;
import maquette.development.specs.steps.WorkspaceStepDefinitions;
import maquette.development.values.WorkspaceMemberRole;
import maquette.testutils.MaquetteContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

/**
 * Workspaces is a core component responsible for managing implementation of use-cases. Workspace defines who has access
 * to which resources and which data assets are being used. It also provides basic services for development of ML models
 * such as MlFlow instances for model tracking.
 */
public abstract class WorkspacesSpecs {

    private WorkspaceStepDefinitions steps;

    private MaquetteContext context;

    @Before
    public void setup() {
        this.context = MaquetteContext.apply();
        this.steps = new WorkspaceStepDefinitions(MaquetteRuntime
            .apply()
            .withModule(MaquetteModelDevelopment.apply(
                setupWorkspacesRepository(), setupModelsRepository(), setupInfrastructurePort(),
                FakeDataAssetsServicePort.apply()))
            .initialize(context.system, context.app));
    }

    @After
    public void clean() {
        this.context.clean();
    }

    public abstract WorkspacesRepository setupWorkspacesRepository();

    public abstract ModelsRepository setupModelsRepository();

    public abstract InfrastructurePort setupInfrastructurePort();

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

        // Given Bobby creates second workspace
        steps.$_creates_a_workspace_with_name_$(context.users.bob, "fake2");

        // When
        steps.$_browses_all_workspaces(context.users.bob);

        // Then
        steps.the_output_should_contain("fake", "fake2");

        // When the Charly becomes a member of the workspace `fake`, she can also see this one.
        steps.$_grants_$_access_to_the_$_workspace_for_$(context.users.bob, WorkspaceMemberRole.MEMBER, "fake",
            context.users.charly);
        steps.$_browses_all_workspaces(context.users.charly);

        // Then
        steps.the_output_should_contain("fake");
        steps.the_output_should_not_contain("fake2");
    }

    /**
     * Workspace creation triggers building of the required infrastructure such as MlFlow
     */
    @Test
    public void workspaceInfrastructure() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_workspace_with_name_$(context.users.bob, "fake");

        // When
        steps.$_browses_all_workspaces(context.users.bob);

        // get environment

    }


    /**
     * Add and remove members from a workspace
     */
    @Test
    public void dataAssetsVisibilityxx() throws ExecutionException, InterruptedException {
// only admins
    }

    /**
     * Verify that the MlFlow parameters are properly set
     */
    @Test
    public void mlFlowParameters() throws ExecutionException, InterruptedException {

    }

    /**
     * Workspaces properties can be managed only by admins
     */
    @Test
    public void dataAssetsVisibilityx() throws ExecutionException, InterruptedException {

    }

    /**
     * List all data access requests and their status that belong to a workspace
     */
    @Test
    public void dataAssetsVisibilityxxxx() throws ExecutionException, InterruptedException {

    }

}
