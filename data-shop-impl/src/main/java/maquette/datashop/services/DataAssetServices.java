package maquette.datashop.services;

import akka.Done;
import maquette.core.MaquetteRuntime;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.user.User;
import maquette.datashop.ports.Workspace;
import maquette.datashop.providers.DataAssetProvider;
import maquette.datashop.values.DataAsset;
import maquette.datashop.values.DataAssetProperties;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.access_requests.DataAccessRequest;
import maquette.datashop.values.access_requests.DataAccessRequestProperties;
import maquette.datashop.values.metadata.DataAssetMetadata;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

public interface DataAssetServices {

    /**
     * Creates a new data asset.
     *
     * @param executor       The user who executes the action.
     * @param type           The data asset type name. The type must be covered by a registered
     *                       {@link DataAssetProvider}.
     * @param metadata       Basic metadata of the asset.
     * @param owner          Optional. The initial owner of the data asset.
     * @param steward        Optional. The initial steward of the data asset.
     * @param customSettings Optional. Custom settings for the data asset type.
     * @return The properties of the newly created  data asset.
     */
    CompletionStage<DataAssetProperties> create(
        User executor, String type, DataAssetMetadata metadata, Authorization owner, Authorization steward,
        @Nullable Object customSettings);

    /**
     * Get details of a data asset by name.
     *
     * @param executor The user who executes the request.
     * @param name     The name of the asset which is queried.
     * @return Details of the data asset.
     */
    CompletionStage<DataAsset> get(User executor, String name);

    /**
     * Get a list of data assets where the executor is owner or member.
     *
     * @param executor The user who executes the request.
     * @return The list of data assets.
     */
    CompletionStage<List<DataAssetProperties>> list(User executor);

    /**
     * Query data assets and get a list of data assets the owner is allowed to view and match the query.
     *
     * @param executor The user who executes the request.
     * @param query The query to filter.
     * @return The list of data assets.
     */
    CompletionStage<List<DataAssetProperties>> query(User executor, String query);

    /**
     * Approve a data asset configuration (usually executed by a data owner)
     *
     * @param executor The user who executes the request.
     * @param name     The name of the data asset to approve.
     * @return Done.
     */
    CompletionStage<Done> approve(User executor, String name);

    /**
     * Decline a data asset configuration (usually executed by a data owner).
     *
     * @param executor The user who executes the request.
     * @param name     The name of teh data asset to decline.
     * @param reason   The reason why the data asset configuration is declined and actions which would allow approval.
     * @return Done.
     */
    CompletionStage<Done> decline(User executor, String name, String reason);

    /**
     * Deprecate a data asset. When a data asset is deprecated it cannot be browsed anymore.
     * Already subscribed users can continue to use it. But no new access requests can be
     * created.
     *
     * @param executor  The user who deprecates the data asset.
     * @param name      The name of the data asset.
     * @param deprecate Whether it should be deprecated or deprecation should be reverted (false)
     * @return Done.
     */
    CompletionStage<Done> deprecate(User executor, String name, boolean deprecate);

    /**
     * Updates a data assets metadata.
     *
     * @param executor The user who executes this action.
     * @param name     The name of the asset to update.
     * @param metadata The updated metadata.
     * @return Done.
     */
    CompletionStage<Done> update(User executor, String name, DataAssetMetadata metadata);

    /**
     * Update a data asset's custom settings.
     *
     * @param executor       The user who executes the action.
     * @param name           The name of the asset to update.
     * @param customSettings The updated custom settings.
     * @return Done.
     */
    CompletionStage<Done> updateCustomSettings(User executor, String name, Object customSettings);

    /**
     * Removes a data asset and deletes all related resources.
     *
     * @param executor The user who executes this action.
     * @param name     The name of the asset to remove.
     * @return Done.
     */
    CompletionStage<Done> remove(User executor, String name);

    /**
     * Request a review of a data owner to check data asset configurations.
     *
     * @param executor The user who executes this action.
     * @param name     The name of the asset which should be reviewed.
     * @param message  An optional message to the reviewer.
     * @return Done.
     */
    CompletionStage<Done> requestReview(User executor, String name, String message);

    /*
     * Access Requests
     */

    /**
     * Creates a new data access request.
     *
     * @param executor  The user who submitted the request.
     * @param name      The name of the data asset for which the access request is targeted.
     * @param workspace The workspace to which the access request is related.
     * @param reason    The reason or justification to access the data.
     * @return The created access request.
     */
    CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String name, String workspace,
                                                                         String reason, MaquetteRuntime runtime);

    /**
     * Get details of a data access request.
     *
     * @param executor The user who requests the information.
     * @param name     The name of the data asset to which the access request belongs to.
     * @param request  The unique id of the access request.
     * @return The full access request.
     */
    CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String name, UID request);

    /**
     * Get a list of data access request for a data asset.
     *
     * @param executor The user who gets the requests.
     * @param name     The name of the data asset for which access requests should be listed.
     * @return The list of access requests (the user can see).
     */
    CompletionStage<List<DataAccessRequestProperties>> getDataAccessRequests(User executor, String name);

    /**
     * Approves a granted data access request.
     *
     * @param executor The user who wants to approve a request. This is usually a data owner.
     * @param name     The name of the data asset to which the access request belongs to.
     * @param request  The unique id of the access request.
     * @param message  An optional message.
     * @return Done.
     */
    CompletionStage<Done> approveDataAccessRequest(
        User executor, String name, UID request, @Nullable String message);

    /**
     * Approve a data access request.
     *
     * @param executor                   The user who approves the request - Grants access to the data.
     * @param name                       The name of the data asset, the access request belongs to.
     * @param request                    The unique id of the access request.
     * @param until                      Optional. A date until access is approved.
     * @param message                    Optional. A message to the consumer of the request.
     * @param environment                Optional. An environment on which it is allowed to use the data.
     * @param downstreamApprovalRequired Optional. Whether additional approvals are required if data products are
     *                                   created on basis of this one.
     * @return Done.
     */
    CompletionStage<Done> grantDataAccessRequest(
        User executor, String name, UID request, @javax.annotation.Nullable Instant until,
        @javax.annotation.Nullable String message,
        String environment, boolean downstreamApprovalRequired);

    /**
     * Reject a data access request.
     *
     * @param executor The user who rejects the request.
     * @param name     The name of the data asset to which the access request belongs.
     * @param request  The unique id of the access request.
     * @param reason   The reason why the request was rejected.
     * @return Done.
     */
    CompletionStage<Done> rejectDataAccessRequest(User executor, String name, UID request, String reason);

    /**
     * Update (resend) a data access request.
     *
     * @param executor The user who updates an existing access request.
     * @param name     The name of the data asset the access request belong to.
     * @param request  The unique id of the access request.
     * @param reason   A reason, additional information or response.
     * @return Done.
     */
    CompletionStage<Done> updateDataAccessRequest(User executor, String name, UID request, String reason);

    /**
     * Withdraw an access request.
     *
     * @param executor The user who withdraws a request.
     * @param name     The name of the data asset the access request belongs to.
     * @param request  The unique id of the access request.
     * @param reason   The reason why the request should be withdrawn.
     * @return Done.
     */
    CompletionStage<Done> withdrawDataAccessRequest(User executor, String name, UID request,
                                                    @javax.annotation.Nullable String reason);

    /*
     * Member management
     */

    /**
     * Grant a data asset member role to another user or group of users.
     *
     * @param executor The user who grants the access.
     * @param name     The name of the data asset.
     * @param member   The member(s) which should be granted.
     * @param role     The role which should be assigned to the new member(s).
     * @return Done.
     */
    CompletionStage<Done> grant(User executor, String name, Authorization member, DataAssetMemberRole role);

    /**
     * Revoke access rights or a member role from a data asset.
     *
     * @param executor The user who revokes access.
     * @param name     The name of the data asset.
     * @param member   The member(s) who should not have access anymore.
     * @return Done.
     */
    CompletionStage<Done> revoke(User executor, String name, Authorization member);

    /**
     * Retrieve a list of known workspaces for a user.
     *
     * @param executor The user who lists its own workspaces.
     */
    CompletionStage<List<Workspace>> getUsersWorkspaces(User executor);
}
