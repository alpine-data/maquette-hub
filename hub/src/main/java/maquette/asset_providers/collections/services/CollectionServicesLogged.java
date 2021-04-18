package maquette.asset_providers.collections.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.data.DataAssetEntity;
import maquette.core.entities.logs.Action;
import maquette.core.entities.logs.ActionCategory;
import maquette.core.entities.logs.Logs;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.values.data.binary.BinaryObject;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class CollectionServicesLogged implements CollectionServices {

   private final CollectionServices delegate;

   private final DataAssetEntities entities;

   private final Logs logs;

   private final DataAssetCompanion assets;

   @Override
   public CompletionStage<List<String>> listFiles(User executor, String collection) {
      return delegate.listFiles(executor, collection);
   }

   @Override
   public CompletionStage<List<String>> listFiles(User executor, String collection, String tag) {
      return delegate.listFiles(executor, collection, tag);
   }

   @Override
   public CompletionStage<Done> put(User executor, String collection, BinaryObject data, String file, String message) {
      var ridCS = entities.getByName(collection).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.put(executor, collection, data, file, message);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         var action = Action.apply(
            ActionCategory.WRITE, "Uploaded file `%s` to collection `%s`",
            file, collection);

         logs.log(executor, action, rid);
         assets.trackProduction(executor, collection);

         return result;
      });
   }

   @Override
   public CompletionStage<Done> putAll(User executor, String collection, BinaryObject data, String basePath, String message) {
      var ridCS = entities.getByName(collection).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.putAll(executor, collection, data, basePath, message);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         var action = Action.apply(
            ActionCategory.WRITE, "Uploaded files to `%s` to collection `%s`",
            basePath, collection);

         logs.log(executor, action, rid);
         assets.trackProduction(executor, collection);

         return result;
      });
   }

   @Override
   public CompletionStage<BinaryObject> readAll(User executor, String collection) {
      var ridCS = entities.getByName(collection).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.readAll(executor, collection);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         var action = Action.apply(
            ActionCategory.READ, "Downloaded files from `%s`", collection);

         logs.log(executor, action, rid);
         assets.trackConsumption(executor, collection);

         return result;
      });
   }

   @Override
   public CompletionStage<BinaryObject> readAll(User executor, String collection, String tag) {
      var ridCS = entities.getByName(collection).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.readAll(executor, collection, tag);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         var action = Action.apply(
            ActionCategory.READ, "Downloaded files of tag `%s` from `%s`", tag, collection);

         logs.log(executor, action, rid);
         assets.trackConsumption(executor, collection);
         return result;
      });
   }

   @Override
   public CompletionStage<BinaryObject> read(User executor, String collection, String file) {
      var ridCS = entities.getByName(collection).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.read(executor, collection, file);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         var action = Action.apply(
            ActionCategory.READ, "Downloaded file `%s` from `%s`", file, collection);

         logs.log(executor, action, rid);
         assets.trackConsumption(executor, collection);
         return result;
      });
   }

   @Override
   public CompletionStage<BinaryObject> read(User executor, String collection, String tag, String file) {
      var ridCS = entities.getByName(collection).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.read(executor, collection, file, tag);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         var action = Action.apply(
            ActionCategory.READ, "Downloaded file `%s` of tag `%s` from `%s`", file, tag, collection);

         logs.log(executor, action, rid);
         assets.trackConsumption(executor, collection);
         return result;
      });
   }

   @Override
   public CompletionStage<Done> remove(User executor, String collection, String file) {
      var ridCS = entities.getByName(collection).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.remove(executor, collection, file);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         var action = Action.apply(
            ActionCategory.WRITE, "Removed file `%s` from `%s`", file, collection);

         logs.log(executor, action, rid);
         assets.trackProduction(executor, collection);
         return result;
      });
   }

   @Override
   public CompletionStage<Done> tag(User executor, String collection, String tag, String message) {
      var ridCS = entities.getByName(collection).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.tag(executor, collection, tag, message);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         var action = Action.apply(
            ActionCategory.WRITE, "Created tag `%s`", tag);

         logs.log(executor, action, rid);
         assets.trackProduction(executor, collection);
         return result;
      });
   }

}
