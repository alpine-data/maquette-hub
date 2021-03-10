package maquette.core.services.data.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasets.DatasetEntity;
import maquette.core.entities.data.datasets.model.Dataset;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasets.model.DatasetVersion;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.data.datasets.model.revisions.Revision;
import maquette.core.entities.data.datasets.model.tasks.Task;
import maquette.core.entities.dependencies.model.DataAssetType;
import maquette.core.entities.logs.Action;
import maquette.core.entities.logs.ActionCategory;
import maquette.core.entities.logs.LogEntry;
import maquette.core.entities.logs.Logs;
import maquette.core.services.dependencies.DependencyCompanion;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.data.*;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import org.apache.avro.Schema;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DatasetServicesLogged implements DatasetServices {

   private final DatasetEntities datasets;

   private final DatasetServices delegate;

   private final Logs logs;

   private final DependencyCompanion dependencies;

   @Override
   public CompletionStage<DatasetProperties> create(User executor, String title, String name, String summary, DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation, DataZone zone, Authorization owner, Authorization steward) {
      return delegate
         .create(executor, title, name, summary, visibility, classification, personalInformation, zone, owner, steward)
         .thenApply(properties -> {
            logs.log(executor, Action.apply(ActionCategory.ADMINISTRATION, "Created dataset `%s`", name), datasets.getResourceUID(properties.getId()));
            return properties;
         });
   }

   @Override
   public CompletionStage<Done> remove(User executor, String dataset) {
      return delegate
         .remove(executor, dataset)
         .thenApply(done -> {
            datasets.getResourceUID(dataset).thenCompose(resource -> logs.log(executor, Action.apply(ActionCategory.ADMINISTRATION, "Deleted dataset `%s`", dataset), resource));
            return done;
         });
   }

   @Override
   public CompletionStage<Dataset> get(User executor, String dataset) {
      return delegate
         .get(executor, dataset)
         .thenApply(result -> {
            logs.log(executor, Action.apply(ActionCategory.VIEW, "Fetched dataset details for dataset `%s`", dataset), datasets.getResourceUID(result.getId()));
            return result;
         });
   }

   @Override
   public CompletionStage<List<DatasetProperties>> list(User executor) {
      return delegate
         .list(executor)
         .thenApply(result -> {
            logs.log(executor, Action.apply(ActionCategory.VIEW, "Fetched available datasets"));
            return result;
         });
   }

   @Override
   public CompletionStage<Done> update(
      User executor,
      String name,
      String updatedName,
      String title,
      String summary,
      DataVisibility visibility,
      DataClassification classification,
      PersonalInformation personalInformation,
      DataZone zone) {

      var previousCS = datasets.getByName(name).thenCompose(DatasetEntity::getProperties);
      var updatedCS = previousCS.thenCompose(i -> delegate
         .update(executor, name, updatedName, title, summary, visibility, classification, personalInformation, zone));

      return Operators.compose(previousCS, updatedCS, (previous, updated) -> {
         var changed = Lists.<String>newArrayList();

         if (!name.equals(updatedName)) {
            changed.add("name");
         }

         if (!previous.getTitle().equals(title)) {
            changed.add("title");
         }

         if (!previous.getSummary().equals(summary)) {
            changed.add("summary");
         }

         if (!previous.getVisibility().equals(visibility)) {
            changed.add("visibility");
         }

         if (!previous.getClassification().equals(classification)) {
            changed.add("classification");
         }

         if (!previous.getPersonalInformation().equals(personalInformation)) {
            changed.add("personalInformation");
         }

         if (!previous.getZone().equals(zone)) {
            changed.add("zone");
         }

         logs.log(
            executor,
            Action.apply(
               ActionCategory.ADMINISTRATION,
               "Updated dataset `%s` properties: %s",
               updatedName,
               String.join(", ", changed)),
            datasets.getResourceUID(previous.getId()));

         return Done.getInstance();
      });
   }

   @Override
   public CompletionStage<Done> approve(User executor, String dataset) {
      return delegate
         .approve(executor, dataset)
         .thenApply(result -> {
            var action = Action.apply(ActionCategory.ADMINISTRATION, "Approved dataset configurations `%s`", dataset);
            datasets.getResourceUID(dataset).thenApply(resource -> logs.log(executor, action, resource));

            return result;
         });
   }

   @Override
   public CompletionStage<Done> deprecate(User executor, String dataset, boolean deprecate) {
      return delegate
         .deprecate(executor, dataset, deprecate)
         .thenApply(result -> {
            var action = Action.apply(ActionCategory.READ, "Deprecated dataset `%s`", dataset);
            datasets.getResourceUID(dataset).thenApply(resource -> logs.log(executor, action, resource));

            return result;
         });
   }

   @Override
   public CompletionStage<List<Task>> getOpenTasks(User executor, String dataset) {
      return delegate
         .getOpenTasks(executor, dataset)
         .thenApply(result -> {
            var action = Action.apply(ActionCategory.VIEW, "Fetched open tasks for asset `%s`", dataset);
            datasets.getResourceUID(dataset).thenApply(resource -> logs.log(executor, action, resource));
            return result;
         });
   }

   @Override
   public CompletionStage<CommittedRevision> commitRevision(User executor, String dataset, UID revision, String message) {
      return delegate
         .commitRevision(executor, dataset, revision, message)
         .thenApply(result -> {
            datasets.getResourceUID(dataset)
               .thenCompose(resource -> logs.log(
                  executor,
                  Action.apply(
                     ActionCategory.WRITE,
                     "Committed dataset version `%s` of dataset `%s`",
                     result.getCommit().map(CommittedRevision::getVersion).map(DatasetVersion::toString).orElse(revision.getValue()),
                     dataset),
                  resource));

            if (executor.getProjectContext().isPresent()) {
               var ctx = executor.getProjectContext().get();
               dependencies.trackProductionByProject(executor, DataAssetType.DATASET, dataset, ctx.getProperties().getName());
            }

            // TODO mw: Add dependency tracking for apps
            // Better else for dependency tracking?

            if (executor instanceof AuthenticatedUser) {
               dependencies.trackProductionByUser(executor, DataAssetType.DATASET, dataset, ((AuthenticatedUser) executor).getId());
            }

            return result;
         });
   }

   @Override
   public CompletionStage<Revision> createRevision(User executor, String dataset, Schema schema) {
      return delegate
         .createRevision(executor, dataset, schema)
         .thenApply(result -> {
            datasets.getResourceUID(dataset)
               .thenCompose(resource -> logs.log(
                  executor,
                  Action.apply(ActionCategory.WRITE, "Created revision `%s` of dataset `%s`", result.getId(), dataset),
                  resource));

            var action = Action.apply(ActionCategory.WRITE, "Uploaded data to revision `%s` from dataset `%s`", result.getId(), dataset);
            datasets.getResourceUID(dataset).thenApply(resource -> logs.log(executor, action, resource));

            return result;
         });
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset, DatasetVersion version) {
      return delegate
         .download(executor, dataset, version)
         .thenApply(result -> {
            var action = Action.apply(ActionCategory.READ, "Downloaded version `%s` from dataset `%s`", version, dataset);
            datasets.getResourceUID(dataset).thenApply(resource -> logs.log(executor, action, resource));

            if (executor.getProjectContext().isPresent()) {
               var ctx = executor.getProjectContext().get();
               dependencies.trackConsumptionByProject(executor, DataAssetType.DATASET, dataset, ctx.getProperties().getName());
            }

            // TODO mw: Add dependency tracking for apps
            // Better else for dependency tracking?

            return result;
         });
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset) {
      return delegate
         .download(executor, dataset)
         .thenApply(result -> {
            var action = Action.apply(ActionCategory.READ, "Downloaded latest version from dataset `%s`", dataset);
            datasets.getResourceUID(dataset).thenApply(resource -> logs.log(executor, action, resource));

            if (executor.getProjectContext().isPresent()) {
               var ctx = executor.getProjectContext().get();
               dependencies.trackConsumptionByProject(executor, DataAssetType.DATASET, dataset, ctx.getProperties().getName());
            }

            // TODO mw: Add dependency tracking for apps
            // Better else for dependency tracking?

            return result;
         });
   }

   @Override
   public CompletionStage<Done> upload(User executor, String dataset, UID revision, Records records) {
      return delegate
         .upload(executor, dataset, revision, records);
   }

   @Override
   public CompletionStage<List<LogEntry>> getAccessLogs(User executor, String asset) {
      return delegate
         .getAccessLogs(executor, asset)
         .thenApply(result -> {
            var action = Action.apply(ActionCategory.VIEW, "Fetched access logs from dataset `%s`", asset);
            datasets.getResourceUID(asset).thenApply(resource -> logs.log(executor, action, resource));
            return result;
         });
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String asset, String project, String reason) {
      return delegate
         .createDataAccessRequest(executor, asset, project, reason)
         .thenApply(result -> {
            var action = Action.apply(ActionCategory.ADMINISTRATION, "Created data access request to `%s` for project `%s`", asset, project);
            datasets.getResourceUID(asset).thenApply(resource -> logs.log(executor, action, resource));
            return result;
         });
   }

   @Override
   public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String asset, UID request) {
      return delegate
         .getDataAccessRequest(executor, asset, request)
         .thenApply(result -> {
            var action = Action.apply(ActionCategory.VIEW, "Fetched access request `%s` from `%s`", request, asset);
            datasets.getResourceUID(asset).thenApply(resource -> logs.log(executor, action, resource));
            return result;
         });
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String asset, UID request, @Nullable Instant until, @Nullable String message) {
      return delegate
         .grantDataAccessRequest(executor, asset, request, until, message)
         .thenApply(result -> {
            var action = Action.apply(ActionCategory.ADMINISTRATION, "Granted data access request to `%s`", request);
            datasets.getResourceUID(asset).thenApply(resource -> logs.log(executor, action, resource));
            return result;
         });
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String asset, UID request, String reason) {
      return delegate
         .rejectDataAccessRequest(executor, asset, request, reason)
         .thenApply(result -> {
            var action = Action.apply(ActionCategory.ADMINISTRATION, "Rejected data access request `%s`", request);
            datasets.getResourceUID(asset).thenApply(resource -> logs.log(executor, action, resource));
            return result;
         });
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String asset, UID request, String reason) {
      return delegate
         .updateDataAccessRequest(executor, asset, request, reason)
         .thenApply(result -> {
            var action = Action.apply(ActionCategory.ADMINISTRATION, "Updated data access request `%s`", request);
            datasets.getResourceUID(asset).thenApply(resource -> logs.log(executor, action, resource));
            return result;
         });
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String asset, UID request, @Nullable String reason) {
      return delegate
         .withdrawDataAccessRequest(executor, asset, request, reason)
         .thenApply(result -> {
            var action = Action.apply(ActionCategory.ADMINISTRATION, "Withdrawn data access request `%s`", request);
            datasets.getResourceUID(asset).thenApply(resource -> logs.log(executor, action, resource));
            return result;
         });
   }

   @Override
   public CompletionStage<Done> grant(User executor, String asset, Authorization member, DataAssetMemberRole role) {
      return delegate
         .grant(executor, asset, member, role)
         .thenApply(result -> {
            var action = Action.apply(ActionCategory.ADMINISTRATION, "Granted `%s` to `%s`", role, member);
            datasets.getResourceUID(asset).thenApply(resource -> logs.log(executor, action, resource));
            return result;
         });
   }

   @Override
   public CompletionStage<Done> revoke(User executor, String asset, Authorization member) {
      return delegate
         .revoke(executor, asset, member)
         .thenApply(result -> {
            var action = Action.apply(ActionCategory.ADMINISTRATION, "Revoked access for `%s`", member);
            datasets.getResourceUID(asset).thenApply(resource -> logs.log(executor, action, resource));
            return result;
         });
   }

}
