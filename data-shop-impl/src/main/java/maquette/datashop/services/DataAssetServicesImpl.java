package maquette.datashop.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.user.User;
import maquette.datashop.entities.DataAssetEntities;
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
      return null;
   }

   @Override
   public CompletionStage<Done> decline(User executor, String name, String reason) {
      return null;
   }

   @Override
   public CompletionStage<Done> deprecate(User executor, String name, boolean deprecate) {
      return null;
   }

   @Override
   public CompletionStage<Done> update(User executor, String name, DataAssetMetadata metadata) {
      return null;
   }

   @Override
   public CompletionStage<Done> updateCustomSettings(User executor, String name, Object customSettings) {
      return null;
   }

   @Override
   public CompletionStage<Done> remove(User executor, String name) {
      return null;
   }

   @Override
   public CompletionStage<Done> requestReview(User executor, String name, String message) {
      return null;
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String name, String project, String reason) {
      return null;
   }

   @Override
   public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String name, UID request) {
      return null;
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String name, UID request, @Nullable Instant until, @Nullable String message, String environment, boolean downstreamApprovalRequired) {
      return null;
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String name, UID request, String reason) {
      return null;
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String name, UID request, String reason) {
      return null;
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String name, UID request, @Nullable String reason) {
      return null;
   }

   @Override
   public CompletionStage<Done> grant(User executor, String name, Authorization member, DataAssetMemberRole role) {
      return null;
   }

   @Override
   public CompletionStage<Done> revoke(User executor, String name, Authorization member) {
      return null;
   }

}
