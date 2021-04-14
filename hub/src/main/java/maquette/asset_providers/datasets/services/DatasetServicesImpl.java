package maquette.asset_providers.datasets.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.asset_providers.datasets.DatasetEntity;
import maquette.asset_providers.datasets.Datasets;
import maquette.asset_providers.datasets.DatasetsRepository;
import maquette.asset_providers.datasets.model.CommittedRevision;
import maquette.asset_providers.datasets.model.DatasetVersion;
import maquette.asset_providers.datasets.model.Revision;
import maquette.common.Operators;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.ports.DataExplorer;
import maquette.core.ports.RecordsStore;
import maquette.core.values.UID;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.User;
import org.apache.avro.Schema;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DatasetServicesImpl implements DatasetServices {

   private final DatasetsRepository repository;

   private final RecordsStore store;

   private final DataExplorer explorer;

   private final DataAssetEntities assets;

   @Override
   public CompletionStage<CommittedRevision> commit(User executor, String dataset, UID revision, String message) {
      var entityCS = getEntity(dataset);

      return entityCS
         .thenCompose(entity -> entity.commit(executor, revision, message))
         .thenApply(rev -> {
            entityCS.thenApply(entity -> entity.analyze(rev.getVersion()));
            return rev;
         });
   }

   @Override
   public CompletionStage<Revision> create(User executor, String dataset, Schema schema) {
      return getEntity(dataset).thenCompose(entity -> entity.createRevision(executor, schema));
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset, DatasetVersion version) {
      return getEntity(dataset).thenCompose(entity -> entity.download(executor, version));
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset) {
      return getEntity(dataset).thenCompose(entity -> entity.download(executor));
   }

   @Override
   public CompletionStage<Done> upload(User executor, String dataset, UID revision, Records records) {
      return getEntity(dataset).thenCompose(entity -> entity.upload(executor, revision, records));
   }

   private CompletionStage<DatasetEntity> getEntity(String dataset) {
      return assets
         .getByName(dataset, Datasets.TYPE_NAME)
         .thenApply(entity -> DatasetEntity.apply(repository, store, explorer, entity));
   }

}
