package maquette.datashop.services;

import akka.Done;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.user.User;
import maquette.datashop.values.DataAsset;
import maquette.datashop.values.DataAssetProperties;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.access_requests.DataAccessRequest;
import maquette.datashop.values.access_requests.DataAccessRequestProperties;
import maquette.datashop.values.metadata.DataAssetMetadata;
import maquette.datashop.values.providers.DataAssetProvider;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

public interface DataAssetServices {

   /**
    * Creates a new data asset.
    *
    * @param executor       The user who executes the action.
    * @param type           The data asset type name. The type must be covered by a registered {@link DataAssetProvider}.
    * @param metadata       Basic metadata of the asset.
    * @param owner          Optional. The initial owner of the data asset.
    * @param steward        Optional. The initial steward of the data asset.
    * @param customSettings Optional. Custom settings for the data asset type.
    * @return The properties of the newly created  data asset.
    */
   CompletionStage<DataAssetProperties> create(
      User executor, String type, DataAssetMetadata metadata, Authorization owner, Authorization steward, @Nullable Object customSettings);

   /**
    * Get details of a data asset by name.
    *
    * @param executor The user who executes the request.
    * @param name     The name of the asset which is queried.
    * @return Details of the data asset.
    */
   CompletionStage<DataAsset> get(User executor, String name);

   /**
    * Get a list of available data assets (the user is allowed to view).
    *
    * @param executor The user who executes the request.
    * @return The list of data assets.
    */
   CompletionStage<List<DataAssetProperties>> list(User executor);

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
    * Deprecate a data asset. This can be executed
    * @param executor
    * @param name
    * @param deprecate
    * @return
    */
   CompletionStage<Done> deprecate(User executor, String name, boolean deprecate);

   CompletionStage<Done> update(User executor, String name, DataAssetMetadata metadata);

   CompletionStage<Done> updateCustomSettings(User executor, String name, Object customSettings);

   CompletionStage<Done> remove(User executor, String name);

   CompletionStage<Done> requestReview(User executor, String name, String message);

   /*
    * Access Requests
    */
   CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String name, String project, String reason);

   CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String name, UID request);

   CompletionStage<Done> grantDataAccessRequest(
      User executor, String name, UID request, @javax.annotation.Nullable Instant until, @javax.annotation.Nullable String message,
      String environment, boolean downstreamApprovalRequired);

   CompletionStage<Done> rejectDataAccessRequest(User executor, String name, UID request, String reason);

   CompletionStage<Done> updateDataAccessRequest(User executor, String name, UID request, String reason);

   CompletionStage<Done> withdrawDataAccessRequest(User executor, String name, UID request, @javax.annotation.Nullable String reason);

   /*
    * Member management
    */
   CompletionStage<Done> grant(User executor, String name, Authorization member, DataAssetMemberRole role);

   CompletionStage<Done> revoke(User executor, String name, Authorization member);

}
