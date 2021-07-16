package maquette.datashop.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.user.User;
import maquette.datashop.api.Workspaces;
import maquette.datashop.entities.DataAssetEntities;
import maquette.datashop.entities.DataAssetEntity;
import maquette.datashop.values.DataAsset;
import maquette.datashop.values.DataAssetProperties;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.access_requests.DataAccessRequest;
import maquette.datashop.values.access_requests.DataAccessRequestProperties;
import maquette.datashop.values.metadata.DataAssetMetadata;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataAssetServicesImpl implements DataAssetServices {

   private final DataAssetEntities entities;

   private final Workspaces workspaces;

   @Override
   public CompletionStage<DataAssetProperties> create(User executor, String type, DataAssetMetadata metadata, Authorization owner, Authorization steward, @Nullable Object customSettings) {
      return entities.create(executor, type, metadata, owner, steward, customSettings);
   }

   @Override
   public CompletionStage<DataAsset> get(User executor, String name) {
      return null;
   }

   @Override
   public CompletionStage<List<DataAssetProperties>> list(User executor) {
      return entities.list();
   }

   @Override
   public CompletionStage<Done> approve(User executor, String name) {
      return entities.getByName(name).thenCompose(entity -> entity.approve(executor));
   }

   @Override
   public CompletionStage<Done> decline(User executor, String name, String reason) {
      // TODO mw: reason is not used yet!?
      return entities.getByName(name).thenCompose(entity -> entity.decline(executor));
   }

   @Override
   public CompletionStage<Done> deprecate(User executor, String name, boolean deprecate) {
      return entities.getByName(name).thenCompose(entity -> entity.deprecate(executor, deprecate));
   }

   @Override
   public CompletionStage<Done> update(User executor, String name, DataAssetMetadata metadata) {
      return entities.getByName(name).thenCompose(entity -> entity.update(executor, metadata));
   }

   @Override
   public CompletionStage<Done> updateCustomSettings(User executor, String name, Object customSettings) {
      return entities.getByName(name).thenCompose(entity -> entity.updateCustomSettings(executor, customSettings));
   }

   @Override
   public CompletionStage<Done> remove(User executor, String name) {
      return entities.removeByName(name);
   }

   @Override
   public CompletionStage<Done> requestReview(User executor, String name, String message) {
      return entities.getByName(name).thenCompose(entity -> entity.requestReview(executor));
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String name, String workspace, String reason) {
      var entityCS = entities.getByName(name);
      var projectEntityCS = workspaces.getWorkspaceByName(workspace);

      return Operators
         .compose(entityCS, projectEntityCS, (entity, projectEntity) -> entity
            .getAccessRequests()
            .createDataAccessRequest(executor, projectEntity.getId(), reason))
         .thenCompose(cs -> cs);
   }

   @Override
   public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String name, UID request) {
      var entityCS = entities.getByName(name);
      var propertiesCS = entityCS.thenCompose(DataAssetEntity::getProperties);
      var accessRequestPropertiesCS = entityCS.thenCompose(a -> a.getAccessRequests().getDataAccessRequestById(request));

      // TODO
      return null;
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String name, UID request, @Nullable Instant until, @Nullable String message, String environment, boolean downstreamApprovalRequired) {
      return entities
         .getByName(name)
         .thenCompose(a -> a.getAccessRequests().grantDataAccessRequest(executor, request, until, message, environment, downstreamApprovalRequired));
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String name, UID request, String reason) {
      return entities
         .getByName(name)
         .thenCompose(a -> a.getAccessRequests().rejectDataAccessRequest(executor, request, reason));
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String name, UID request, String reason) {
      return entities
         .getByName(name)
         .thenCompose(a -> a.getAccessRequests().updateDataAccessRequest(executor, request, reason));
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String name, UID request, @Nullable String reason) {
      return entities
         .getByName(name)
         .thenCompose(a -> a.getAccessRequests().withdrawDataAccessRequest(executor, request, reason));
   }

   @Override
   public CompletionStage<Done> grant(User executor, String name, Authorization member, DataAssetMemberRole role) {
      return entities
         .getByName(name)
         .thenCompose(e -> e.getMembers().addMember(executor, member, role));
   }

   @Override
   public CompletionStage<Done> revoke(User executor, String name, Authorization member) {
      return entities
         .getByName(name)
         .thenCompose(e -> e.getMembers().removeMember(executor, member));
   }

}
