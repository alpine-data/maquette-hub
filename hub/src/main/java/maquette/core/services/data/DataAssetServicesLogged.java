package maquette.core.services.data;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.data.DataAssetEntity;
import maquette.core.entities.data.model.DataAsset;
import maquette.core.entities.data.model.DataAssetMetadata;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.entities.data.model.access.DataAccessRequest;
import maquette.core.entities.data.model.access.DataAccessRequestProperties;
import maquette.core.entities.logs.Action;
import maquette.core.entities.logs.ActionCategory;
import maquette.core.entities.logs.LogEntry;
import maquette.core.entities.logs.Logs;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.user.User;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataAssetServicesLogged implements DataAssetServices {

   private final DataAssetEntities entities;

   private final DataAssetServices delegate;

   private final Logs logs;

   @Override
   public CompletionStage<DataAssetProperties> create(User executor, String type, DataAssetMetadata metadata, Authorization owner, Authorization steward, @Nullable Object customSettings) {
      return delegate
         .create(executor, type, metadata, owner, steward, customSettings)
         .thenCompose(properties -> entities
            .getById(properties.getId())
            .getResourceId()
            .thenApply(rid -> {
               logs.log(
                  executor,
                  Action.apply(ActionCategory.ADMINISTRATION, "Created data asset `%s` of type `%s`", metadata.getName(), type), rid);

               return properties;
            }));
   }

   @Override
   public CompletionStage<DataAsset> get(User executor, String name) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.get(executor, name);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         logs.log(
            executor,
            Action.apply(ActionCategory.VIEW, "Fetched data asset details for `%s`", name), rid);

         return result;
      });
   }

   @Override
   public CompletionStage<List<DataAssetProperties>> list(User executor) {
      return delegate
         .list(executor)
         .thenApply(result -> {
            logs.log(executor, Action.apply(ActionCategory.VIEW, "Fetched available data assets"));
            return result;
         });
   }

   @Override
   public CompletionStage<Done> approve(User executor, String name) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.approve(executor, name);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         logs.log(
            executor,
            Action.apply(ActionCategory.ADMINISTRATION, "Approved data asset configurations `%s`", name),
            rid);

         return result;
      });
   }

   @Override
   public CompletionStage<Done> decline(User executor, String name, String reason) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.decline(executor, name, reason);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         logs.log(
            executor,
            Action.apply(ActionCategory.ADMINISTRATION, "Declined data asset configurations `%s`. Reason: `%s`", name, reason),
            rid);

         return result;
      });
   }

   @Override
   public CompletionStage<Done> deprecate(User executor, String name, boolean deprecate) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.approve(executor, name);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         logs.log(
            executor,
            Action.apply(ActionCategory.ADMINISTRATION, "Deprecated data asset configurations `%s`", name),
            rid);

         return result;
      });
   }

   @Override
   public CompletionStage<Done> update(User executor, String name, DataAssetMetadata metadata) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var previousCS = entities.getByName(name).thenCompose(DataAssetEntity::getProperties);
      var resultCS = delegate.update(executor, name, metadata);

      return Operators.compose(ridCS, previousCS, resultCS, (rid, previous, result) -> {
         var changed = Lists.<String>newArrayList();

         if (!previous.getMetadata().getName().equals(name)) {
            changed.add("name");
         }

         if (!previous.getMetadata().getTitle().equals(metadata.getTitle())) {
            changed.add("title");
         }

         if (!previous.getMetadata().getSummary().equals(metadata.getSummary())) {
            changed.add("summary");
         }

         if (!previous.getMetadata().getVisibility().equals(metadata.getVisibility())) {
            changed.add("visibility");
         }

         if (!previous.getMetadata().getClassification().equals(metadata.getClassification())) {
            changed.add("classification");
         }

         if (!previous.getMetadata().getPersonalInformation().equals(metadata.getPersonalInformation())) {
            changed.add("personalInformation");
         }

         if (!previous.getMetadata().getZone().equals(metadata.getZone())) {
            changed.add("zone");
         }

         logs.log(
            executor,
            Action.apply(ActionCategory.ADMINISTRATION, "Updated data asset properties of `%s`: %s", metadata.getName(), String.join(", ", changed)),
            rid);

         return result;
      });
   }

   @Override
   public CompletionStage<Done> updateCustomSettings(User executor, String name, Object customSettings) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.updateCustomSettings(executor, name, customSettings);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         logs.log(
            executor,
            Action.apply(ActionCategory.ADMINISTRATION, "Updated custom properties for `%s`", name),
            rid);

         return result;
      });
   }

   @Override
   public CompletionStage<Done> remove(User executor, String name) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.remove(executor, name);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         logs.log(
            executor,
            Action.apply(ActionCategory.ADMINISTRATION, "Removed data asset `%s`", name),
            rid);

         return result;
      });
   }

   @Override
   public CompletionStage<Done> requestReview(User executor, String name, String message) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.requestReview(executor, name, message);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         logs.log(
            executor,
            Action.apply(ActionCategory.ADMINISTRATION, "Requested review for data asset `%s`. Message: `%s`", name, message),
            rid);

         return result;
      });
   }

   @Override
   public CompletionStage<List<LogEntry>> getAccessLogs(User executor, String name) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.getAccessLogs(executor, name);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         logs.log(
            executor,
            Action.apply(ActionCategory.VIEW, "Fetched access logs from data asset `%s`", name),
            rid);

         return result;
      });
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String name, String project, String reason) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.createDataAccessRequest(executor, name, project, reason);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         logs.log(
            executor,
            Action.apply(ActionCategory.ADMINISTRATION, "Created data access request to `%s` for project `%s`", name, project),
            rid);

         return result;
      });
   }

   @Override
   public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String name, UID request) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.getDataAccessRequest(executor, name, request);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         logs.log(
            executor,
            Action.apply(ActionCategory.VIEW, "Fetched access request `%s` from `%s`", request, name),
            rid);

         return result;
      });
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String name, UID request, @Nullable Instant until, @Nullable String message) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.grantDataAccessRequest(executor, name, request, until, message);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         logs.log(
            executor,
            Action.apply(ActionCategory.ADMINISTRATION, "Granted access request `%s` to `%s`", request, name),
            rid);

         return result;
      });
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String name, UID request, String reason) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.rejectDataAccessRequest(executor, name, request, reason);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         logs.log(
            executor,
            Action.apply(ActionCategory.ADMINISTRATION, "Rejected access request `%s` to `%s`", request, name),
            rid);

         return result;
      });
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String name, UID request, String reason) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.updateDataAccessRequest(executor, name, request, reason);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         logs.log(
            executor,
            Action.apply(ActionCategory.VIEW, "Updated access request `%s` to `%s`", request, name),
            rid);

         return result;
      });
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String name, UID request, @Nullable String reason) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.withdrawDataAccessRequest(executor, name, request, reason);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         logs.log(
            executor,
            Action.apply(ActionCategory.VIEW, "Withdrew access request `%s` to `%s`", request, name),
            rid);

         return result;
      });
   }

   @Override
   public CompletionStage<Done> grant(User executor, String name, Authorization member, DataAssetMemberRole role) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.grant(executor, name, member, role);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         logs.log(
            executor,
            Action.apply(ActionCategory.VIEW, "Granted `%s` to `%s`", role, member),
            rid);

         return result;
      });
   }

   @Override
   public CompletionStage<Done> revoke(User executor, String name, Authorization member) {
      var ridCS = entities.getByName(name).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.revoke(executor, name, member);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         logs.log(
            executor,
            Action.apply(ActionCategory.VIEW, "Revoked access for `%s`", member),
            rid);

         return result;
      });
   }

}
