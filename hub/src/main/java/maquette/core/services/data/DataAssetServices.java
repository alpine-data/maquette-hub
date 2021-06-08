package maquette.core.services.data;

import akka.Done;
import maquette.core.entities.data.model.DataAsset;
import maquette.core.entities.data.model.DataAssetMetadata;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.entities.data.model.access.DataAccessRequest;
import maquette.core.entities.data.model.access.DataAccessRequestProperties;
import maquette.core.entities.logs.LogEntry;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.user.User;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

public interface DataAssetServices {

   /*
    * Manage data asset
    */
   CompletionStage<DataAssetProperties> create(
      User executor, String type, DataAssetMetadata metadata, Authorization owner, Authorization steward, @Nullable Object customSettings);

   CompletionStage<DataAsset> get(User executor, String name);

   CompletionStage<List<DataAssetProperties>> list(User executor);

   CompletionStage<Done> approve(User executor, String name);

   CompletionStage<Done> decline(User executor, String name, String reason);

   CompletionStage<Done> deprecate(User executor, String name, boolean deprecate);

   CompletionStage<Done> update(User executor, String name, DataAssetMetadata metadata);

   CompletionStage<Done> updateCustomSettings(User executor, String name, Object customSettings);

   CompletionStage<Done> remove(User executor, String name);

   CompletionStage<Done> requestReview(User executor, String name, String message);

   /*
    * Access Requests
    */
   CompletionStage<List<LogEntry>> getAccessLogs(User executor, String name);

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
