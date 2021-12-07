package maquette.datashop.specs;

import maquette.core.MaquetteRuntime;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.ports.FakeWorkspacesServicePort;
import maquette.datashop.providers.FakeProvider;
import maquette.datashop.specs.steps.DataAssetStepDefinitions;
import maquette.datashop.values.metadata.DataVisibility;
import maquette.testutils.MaquetteContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

/**
 * Data assets represent all different kind of data which can be managed with Maquette DataShop. There are a bunch of
 * operations which are common across
 * all data assets, e.g. Metadata management, Access Request procedures.
 */
public abstract class DataAssetsSpecs {

    private DataAssetStepDefinitions steps;

    private MaquetteContext context;

    @Before
    public void setup() {
        this.context = MaquetteContext.apply();

        var workspaces = FakeWorkspacesServicePort.apply();
        var dataAssetsRepository = setupDataAssetsRepository();
        var runtime = MaquetteRuntime
            .apply()
            .withModule(MaquetteDataShop.apply(dataAssetsRepository, workspaces, FakeProvider.apply()))
            .initialize(context.system, context.app);

        this.steps = new DataAssetStepDefinitions(runtime, workspaces);
    }

    @After
    public void clean() {
        this.context.clean();
    }

    public abstract DataAssetsRepository setupDataAssetsRepository();

    /**
     * Data Assets can be private or public. The metadata of public assets can be seen by all users, private assets
     * can not be seen by other users.
     */
    @Test
    public void dataAssetsVisibility() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_data_asset_of_type_$_with_name_$(context.users.bob, "fake", "some-asset");
        steps.$_creates_a_data_asset_of_type_$_with_name_$(context.users.charly, "fake", "some-other-asset");
        steps.$_configures_data_asset_$_to_be_$(context.users.bob, "some-asset", DataVisibility.PRIVATE);

        // When
        steps.$_browses_all_data_assets(context.users.bob);

        // Then
        steps.the_output_should_contain("some-asset", "some-other-asset");

        // When
        steps.$_browses_all_data_assets(context.users.charly);

        // Then
        steps.the_output_should_contain("some-other-asset");
        steps.the_output_should_not_contain("some-asset");

        /*
         * When the Charly becomes a member of `some-asset`, she can also see this one.
         */
        steps.$_grants_consumer_access_rights_for_$(context.users.bob, "some-asset", context.users.charly);
        steps.$_browses_all_data_assets(context.users.charly);
        steps.the_output_should_contain("some-asset", "some-other-asset");
        steps.$_should_be_able_to_read_asset_$(context.users.charly, "some-asset");
    }

    /**
     * Data Assets can be discovered by any user (except they have a private visibility).
     * To get access to these assets, users can request access, data owners and stewards can respond to these
     * requests and grant or reject the access.
     *
     * Data Access requests are always created on behalf of a workspace, which defines the use-case or initiative
     * for which the data should be used. Thus, also other members of the workspace can view the access request or
     * respond to it.
     */
    @Test
    public void dataAssetsAccessRequestProcess() throws ExecutionException, InterruptedException {
        // Given
        steps.$_creates_a_data_asset_of_type_$_with_name_$(context.users.bob, "fake", "some-asset");
        steps.$_is_member_of_workspace_$(context.users.charly, "some-workspace");
        steps.$_requests_access_for_asset_$_on_behalf_of_$(context.users.charly, "some-asset", "some-workspace");

        // When
        steps.$_lists_access_requests_for_asset_$(context.users.bob, "some-asset");

        // Then
        steps.the_output_should_contain_the_access_request_of(context.users.charly);

        // When
        steps.$_grants_this_access_request(context.users.bob);

        // Then
        steps.$_should_be_able_to_read_asset_$(context.users.charly, "some-asset");
    }

    /**
     * If an access request is made for a data asset which contains sensitive information (e.g. confidential data,
     * or personal information). The access request must be reviewed by the data owner as well before access is possible.
     */
    @Test
    public void dataAssetsAccessRequestProcessWithSensitiveInformation() throws ExecutionException,
        InterruptedException {

        // Given
        steps.$_creates_a_data_asset_with_sensitive_data_of_type_$_with_name_$(context.users.bob, "fake", "some-asset");
        steps.$_is_data_owner_of_data_asset_$(context.users.alice, "some-asset");
        steps.$_is_member_of_workspace_$(context.users.charly, "some-workspace");
        steps.$_requests_access_for_asset_$_on_behalf_of_$(context.users.charly, "some-asset", "some-workspace");

        // When
        steps.$_lists_access_requests_for_asset_$(context.users.bob, "some-asset");

        // Then
        steps.the_output_should_contain_the_access_request_of(context.users.charly);

        // When
        steps.$_grants_this_access_request(context.users.bob);

        // Then
        steps.$_should_not_be_able_to_read_asset_$(context.users.charly, "some-asset");

        // When
        steps.$_approves_the_access_request(context.users.alice);

        // Then
        steps.$_should_be_able_to_read_asset_$(context.users.charly, "some-asset");
    }

}
