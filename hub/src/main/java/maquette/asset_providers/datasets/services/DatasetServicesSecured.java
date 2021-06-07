package maquette.asset_providers.datasets.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.asset_providers.datasets.model.CommittedRevision;
import maquette.asset_providers.datasets.model.DatasetVersion;
import maquette.asset_providers.datasets.model.Revision;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetPermissions;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.User;
import org.apache.avro.Schema;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DatasetServicesSecured implements DatasetServices {

   private final DatasetServices delegate;

   private final DataAssetCompanion comp;

   @Override
   public CompletionStage<Done> analyze(User executor, String dataset, DatasetVersion version) {
      return comp
         .withAuthorization(() -> comp.hasPermission(executor, dataset, DataAssetPermissions::canChangeSettings))
         .thenCompose(ok -> delegate.analyze(executor, dataset, version));
   }

   @Override
   public CompletionStage<CommittedRevision> commit(User executor, String dataset, UID revision, String message) {
      return comp
         .withAuthorization(() -> comp.hasPermission(executor, dataset, DataAssetPermissions::canProduce))
         .thenCompose(ok -> delegate.commit(executor, dataset, revision, message));
   }

   @Override
   public CompletionStage<Revision> create(User executor, String dataset, Schema schema) {
      return comp
         .withAuthorization(() -> comp.hasPermission(executor, dataset, DataAssetPermissions::canProduce))
         .thenCompose(ok -> delegate.create(executor, dataset, schema));
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset, DatasetVersion version) {
      return comp
         .withAuthorization(
            () -> comp.hasPermission(executor, dataset, DataAssetPermissions::canConsume),
            () -> CompletableFuture.completedFuture(executor.isSystemUser()),
            () -> comp.isSubscribedConsumer(executor, dataset))
         .thenCompose(ok -> delegate.download(executor, dataset, version));
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset) {
      return comp
         .withAuthorization(
            () -> comp.hasPermission(executor, dataset, DataAssetPermissions::canConsume),
            () -> comp.isSubscribedConsumer(executor, dataset))
         .thenCompose(ok -> delegate.download(executor, dataset));
   }

   @Override
   public CompletionStage<Done> upload(User executor, String dataset, UID revision, Records records) {
      return comp
         .withAuthorization(() -> comp.hasPermission(executor, dataset, DataAssetPermissions::canConsume))
         .thenCompose(ok -> delegate.upload(executor, dataset, revision, records));
   }

}
