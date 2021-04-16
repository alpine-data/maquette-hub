package maquette.asset_providers.datasets.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.asset_providers.datasets.model.CommittedRevision;
import maquette.asset_providers.datasets.model.DatasetVersion;
import maquette.asset_providers.datasets.model.Revision;
import maquette.common.Operators;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.data.DataAssetEntity;
import maquette.core.entities.logs.Action;
import maquette.core.entities.logs.ActionCategory;
import maquette.core.entities.logs.Logs;
import maquette.core.services.dependencies.DependencyCompanion;
import maquette.core.values.UID;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import org.apache.avro.Schema;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DatasetServicesLogged implements DatasetServices {

   private final DatasetServices delegate;

   private final DataAssetEntities entities;

   private final Logs logs;

   private final DependencyCompanion dependencies;

   @Override
   public CompletionStage<Done> analyze(User executor, String dataset, DatasetVersion version) {
      return delegate.analyze(executor, dataset, version);
   }

   @Override
   public CompletionStage<CommittedRevision> commit(User executor, String dataset, UID revision, String message) {
      var ridCS = entities.getByName(dataset).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.commit(executor, dataset, revision, message);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         var action = Action.apply(
            ActionCategory.WRITE, "Committed dataset version `%s` of dataset `%s`",
            result.getCommit().map(CommittedRevision::getVersion).map(DatasetVersion::toString).orElse(revision.getValue()),
            dataset);

         logs.log(executor, action, rid);

         if (executor.getProjectContext().isPresent()) {
            var ctx = executor.getProjectContext().get();
            dependencies.trackProductionByProject(executor, dataset, ctx.getProperties().getName());
         }

         // TODO mw: Add dependency tracking for apps
         // Better else for dependency tracking?

         if (executor instanceof AuthenticatedUser) {
            dependencies.trackProductionByUser(executor, dataset, ((AuthenticatedUser) executor).getId());
         }

         return result;
      });
   }

   @Override
   public CompletionStage<Revision> create(User executor, String dataset, Schema schema) {
      var ridCS = entities.getByName(dataset).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.create(executor, dataset, schema);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         var created = Action.apply(
            ActionCategory.WRITE, "Created revision `%s` of dataset `%s`", result.getId(), dataset);

         var uploaded = Action.apply(
            ActionCategory.WRITE, "Uploaded data to revision `%s` from dataset `%s`", result.getId(), dataset);

         logs.log(executor, created, rid);
         logs.log(executor, uploaded, rid);

         return result;
      });
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset, DatasetVersion version) {
      var ridCS = entities.getByName(dataset).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.download(executor, dataset, version);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         var action = Action.apply(ActionCategory.READ, "Downloaded version `%s` from dataset `%s`", version, dataset);
         logs.log(executor, action, rid);

         if (executor.getProjectContext().isPresent()) {
            var ctx = executor.getProjectContext().get();
            dependencies.trackConsumptionByProject(executor, dataset, ctx.getProperties().getName());
         }

         // TODO mw: Add dependency tracking for apps

         return result;
      });
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset) {
      var ridCS = entities.getByName(dataset).thenCompose(DataAssetEntity::getResourceId);
      var resultCS = delegate.download(executor, dataset);

      return Operators.compose(ridCS, resultCS, (rid, result) -> {
         var action = Action.apply(ActionCategory.READ, "Downloaded latest version from dataset `%s`", dataset);
         logs.log(executor, action, rid);

         if (executor.getProjectContext().isPresent()) {
            var ctx = executor.getProjectContext().get();
            dependencies.trackConsumptionByProject(executor, dataset, ctx.getProperties().getName());
         }

         // TODO mw: Add dependency tracking for apps

         return result;
      });
   }

   @Override
   public CompletionStage<Done> upload(User executor, String dataset, UID revision, Records records) {
      return delegate.upload(executor, dataset, revision, records);
   }

}
