package maquette.core.services.data.collections;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.data.collections.CollectionEntities;
import maquette.core.entities.data.collections.CollectionEntity;
import maquette.core.entities.data.collections.model.Collection;
import maquette.core.entities.data.collections.model.CollectionProperties;
import maquette.core.entities.data.datasets.model.tasks.Task;
import maquette.core.services.data.DataAssetServices;
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
public final class CollectionServicesImpl implements CollectionServices {

   private final CollectionEntities entities;

   private final DataAssetServices<CollectionProperties, CollectionEntity> assets;

   private final CollectionCompanion comp;

   @Override
   public CompletionStage<CollectionProperties> create(
      User executor, String title, String name, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation,
      DataZone zone, Authorization owner, Authorization steward) {

      return entities.create(executor, title, name, summary, visibility, classification, personalInformation, zone, owner, steward);
   }

   @Override
   public CompletionStage<Collection> get(User executor, String asset) {
      return assets.get(executor, asset, comp::mapEntityToAsset);
   }

   @Override
   public CompletionStage<List<CollectionProperties>> list(User executor) {
      return assets.list(executor);
   }

   @Override
   public CompletionStage<Done> remove(User executor, String asset) {
      return assets.remove(executor, asset);
   }

   @Override
   public CompletionStage<Done> update(
      User executor, String name, String updatedName, String title, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation, DataZone zone) {

      return entities
         .getByName(name)
         .thenCompose(as -> as.update(executor, name, title, summary, visibility, classification, personalInformation, zone));
   }

   @Override
   public CompletionStage<Done> approve(User executor, String collection) {
      return assets.approve(executor, collection);
   }

   @Override
   public CompletionStage<Done> deprecate(User executor, String collection, boolean deprecate) {
      return assets.deprecate(executor, collection, deprecate);
   }

   @Override
   public CompletionStage<List<Task>> getOpenTasks(User executor, String collection) {
      return assets.getOpenTasks(executor, collection);
   }

   @Override
   public CompletionStage<List<String>> listFiles(User executor, String collection) {
      return entities
         .getByName(collection)
         .thenCompose(as -> as.getFiles().list());
   }

   @Override
   public CompletionStage<List<String>> listFiles(User executor, String collection, String tag) {
      return entities
         .getByName(collection)
         .thenCompose(as -> as.getFiles().list(tag));
   }

   @Override
   public CompletionStage<Done> put(User executor, String collection, BinaryObject data, String file, String message) {
      return entities
         .getByName(collection)
         .thenCompose(col -> col.getFiles().put(executor, data, file, message));
   }

   @Override
   public CompletionStage<Done> putAll(User executor, String collection, BinaryObject data, String basePath, String message) {
      return entities
         .getByName(collection)
         .thenCompose(col -> col.getFiles().putAll(executor, data, basePath, message));
   }

   @Override
   public CompletionStage<BinaryObject> readAll(User executor, String collection) {
      return entities
         .getByName(collection)
         .thenCompose(col -> col.getFiles().readAll(executor));
   }

   @Override
   public CompletionStage<BinaryObject> readAll(User executor, String collection, String tag) {
      return entities
         .getByName(collection)
         .thenCompose(col -> col.getFiles().readAll(executor, tag));
   }

   @Override
   public CompletionStage<BinaryObject> read(User executor, String collection, String file) {
      return entities
         .getByName(collection)
         .thenCompose(col -> col.getFiles().read(executor, file));
   }

   @Override
   public CompletionStage<BinaryObject> read(User executor, String collection, String tag, String file) {
      return entities
         .getByName(collection)
         .thenCompose(col -> col.getFiles().read(executor, tag, file));
   }

   @Override
   public CompletionStage<Done> remove(User executor, String collection, String file) {
      return entities
         .getByName(collection)
         .thenCompose(col -> col.getFiles().remove(executor, file));
   }

   @Override
   public CompletionStage<Done> tag(User executor, String collection, String tag, String message) {
      return entities
         .getByName(collection)
         .thenCompose(col -> col.getFiles().tag(executor, tag, message));
   }

   @Override
   public CompletionStage<List<DataAccessLogEntry>> getAccessLogs(User executor, String asset) {
      return assets.getAccessLogs(executor, asset);
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String asset, String project, String reason) {
      return assets.createDataAccessRequest(executor, asset, project, reason);
   }

   @Override
   public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String asset, UID request) {
      return assets.getDataAccessRequest(executor, asset, request);
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String asset, UID request, @Nullable Instant until, @Nullable String message) {
      return assets.grantDataAccessRequest(executor, asset, request, until, message);
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String asset, UID request, String reason) {
      return assets.rejectDataAccessRequest(executor, asset, request, reason);
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String asset, UID request, String reason) {
      return assets.updateDataAccessRequest(executor, asset, request, reason);
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String asset, UID request, @Nullable String reason) {
      return assets.withdrawDataAccessRequest(executor, asset, request, reason);
   }

   @Override
   public CompletionStage<Done> grant(User executor, String asset, Authorization member, DataAssetMemberRole role) {
      return assets.grant(executor, asset, member, role);
   }

   @Override
   public CompletionStage<Done> revoke(User executor, String asset, Authorization member) {
      return assets.revoke(executor, asset, member);
   }
}
