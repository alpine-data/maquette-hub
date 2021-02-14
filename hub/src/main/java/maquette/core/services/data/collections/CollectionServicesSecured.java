package maquette.core.services.data.collections;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.data.collections.CollectionEntities;
import maquette.core.entities.data.collections.model.Collection;
import maquette.core.entities.data.collections.model.CollectionProperties;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.data.*;
import maquette.core.values.data.binary.BinaryObject;
import maquette.core.values.data.logs.DataAccessLogEntry;
import maquette.core.values.user.User;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class CollectionServicesSecured implements CollectionServices {

   private final CollectionServices delegate;

   private final CollectionCompanion companion;

   private final DataAssetCompanion<CollectionProperties, CollectionEntities> assets;

   @Override
   public CompletionStage<CollectionProperties> create(
      User executor, String title, String name, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation,
      DataZone zone, Authorization owner, Authorization steward) {

      return companion
         .withAuthorization(() -> companion.isAuthenticatedUser(executor))
         .thenCompose(ok -> delegate.create(
            executor, title, name, summary, visibility, classification, personalInformation, zone, owner, steward));
   }

   @Override
   public CompletionStage<Collection> get(User executor, String asset) {
      return delegate.get(executor, asset);
   }

   @Override
   public CompletionStage<List<CollectionProperties>> list(User executor) {
      return delegate.list(executor);
   }

   @Override
   public CompletionStage<Done> remove(User executor, String asset) {
      return delegate.remove(executor, asset);
   }

   @Override
   public CompletionStage<Done> update(User executor, String name, String updatedName, String title, String summary, DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {
      return delegate.update(executor, name, updatedName, title, summary, visibility, classification, personalInformation);
   }

   @Override
   public CompletionStage<List<String>> listFiles(User executor, String collection) {
      return companion
         .withAuthorization(
            () -> assets.isMember(executor, collection, DataAssetMemberRole.OWNER),
            () -> assets.isMember(executor, collection, DataAssetMemberRole.MEMBER),
            () -> assets.isMember(executor, collection, DataAssetMemberRole.CONSUMER))
         .thenCompose(ok -> delegate.listFiles(executor, collection));
   }

   @Override
   public CompletionStage<List<String>> listFiles(User executor, String collection, String tag) {
      return companion
         .withAuthorization(
            () -> assets.isMember(executor, collection, DataAssetMemberRole.OWNER),
            () -> assets.isMember(executor, collection, DataAssetMemberRole.MEMBER),
            () -> assets.isMember(executor, collection, DataAssetMemberRole.CONSUMER))
         .thenCompose(ok -> delegate.listFiles(executor, collection, tag));
   }

   @Override
   public CompletionStage<Done> put(User executor, String collection, BinaryObject data, String file, String message) {
      return companion
         .withAuthorization(
            () -> assets.isMember(executor, collection, DataAssetMemberRole.OWNER),
            () -> assets.isMember(executor, collection, DataAssetMemberRole.MEMBER),
            () -> assets.isMember(executor, collection, DataAssetMemberRole.PRODUCER))
         .thenCompose(ok -> delegate.put(executor, collection, data, file, message));
   }

   @Override
   public CompletionStage<Done> putAll(User executor, String collection, BinaryObject data, String basePath, String message) {
      // TODO
      return delegate.putAll(executor, collection, data, basePath, message);
   }

   @Override
   public CompletionStage<BinaryObject> readAll(User executor, String collection) {
      // TODO
      return delegate.readAll(executor, collection);
   }

   @Override
   public CompletionStage<BinaryObject> readAll(User executor, String collection, String tag) {
      return delegate.readAll(executor, collection, tag);
   }

   @Override
   public CompletionStage<BinaryObject> read(User executor, String collection, String file) {
      return companion
         .withAuthorization(
            () -> assets.isSubscribedConsumer(executor, collection),
            () -> assets.isMember(executor, collection, DataAssetMemberRole.OWNER),
            () -> assets.isMember(executor, collection, DataAssetMemberRole.MEMBER),
            () -> assets.isMember(executor, collection, DataAssetMemberRole.CONSUMER))
         .thenCompose(ok -> delegate.read(executor, collection, file));
   }

   @Override
   public CompletionStage<BinaryObject> read(User executor, String collection, String tag, String file) {
      return companion
         .withAuthorization(
            () -> assets.isSubscribedConsumer(executor, collection),
            () -> assets.isMember(executor, collection, DataAssetMemberRole.OWNER),
            () -> assets.isMember(executor, collection, DataAssetMemberRole.MEMBER),
            () -> assets.isMember(executor, collection, DataAssetMemberRole.CONSUMER))
         .thenCompose(ok -> delegate.read(executor, collection, tag, file));
   }

   @Override
   public CompletionStage<Done> remove(User executor, String collection, String file) {
      return companion
         .withAuthorization(
            () -> assets.isMember(executor, collection, DataAssetMemberRole.OWNER),
            () -> assets.isMember(executor, collection, DataAssetMemberRole.MEMBER),
            () -> assets.isMember(executor, collection, DataAssetMemberRole.PRODUCER))
         .thenCompose(ok -> delegate.remove(executor, collection, file));
   }

   @Override
   public CompletionStage<Done> tag(User executor, String collection, String tag, String message) {
      return companion
         .withAuthorization(
            () -> assets.isMember(executor, collection, DataAssetMemberRole.OWNER),
            () -> assets.isMember(executor, collection, DataAssetMemberRole.MEMBER),
            () -> assets.isMember(executor, collection, DataAssetMemberRole.PRODUCER))
         .thenCompose(ok -> delegate.tag(executor, collection, tag, message));
   }

   @Override
   public CompletionStage<List<DataAccessLogEntry>> getAccessLogs(User executor, String asset) {
      return delegate.getAccessLogs(executor, asset);
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String asset, String project, String reason) {
      return delegate.createDataAccessRequest(executor, asset, project, reason);
   }

   @Override
   public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String asset, UID request) {
      return delegate.getDataAccessRequest(executor, asset, request);
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String asset, UID request, @Nullable Instant until, @Nullable String message) {
      return delegate.grantDataAccessRequest(executor, asset, request, until, message);
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String asset, UID request, String reason) {
      return delegate.rejectDataAccessRequest(executor, asset, request, reason);
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String asset, UID request, String reason) {
      return delegate.updateDataAccessRequest(executor, asset, request, reason);
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String asset, UID request, @Nullable String reason) {
      return delegate.withdrawDataAccessRequest(executor, asset, request, reason);
   }

   @Override
   public CompletionStage<Done> grant(User executor, String asset, Authorization member, DataAssetMemberRole role) {
      return delegate.grant(executor, asset, member, role);
   }

   @Override
   public CompletionStage<Done> revoke(User executor, String asset, Authorization member) {
      return delegate.revoke(executor, asset, member);
   }
}
