package maquette.core.services.data.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasets.DatasetEntity;
import maquette.core.entities.data.datasets.model.Dataset;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasets.model.DatasetVersion;
import maquette.core.entities.data.datasets.model.records.Records;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.data.datasets.model.revisions.Revision;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.services.data.DataAssetServices;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;
import org.apache.avro.Schema;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DatasetServicesImpl implements DatasetServices {

   private final DatasetEntities datasets;

   private final ProcessManager processes;

   private final DataAssetServices<DatasetProperties, DatasetEntity> assets;

   private final DatasetCompanion companion;

   @Override
   public CompletionStage<DatasetProperties> create(User executor, String title, String name, String summary, DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {
      return datasets
         .create(executor, title, name, summary, visibility, classification, personalInformation);
   }

   @Override
   public CompletionStage<Done> remove(User executor, String dataset) {
      return assets.remove(executor, dataset);
   }

   @Override
   public CompletionStage<Dataset> get(User executor, String dataset) {
      return assets.get(executor, dataset, companion::mapEntityToDataset);
   }

   @Override
   public CompletionStage<List<DatasetProperties>> list(User executor) {
      return assets.list(executor);
   }

   @Override
   public CompletionStage<Done> update(User executor, String dataset, String updatedName, String title, String summary, DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {
      return datasets
         .getByName(dataset)
         .thenCompose(ds -> ds.update(executor, updatedName, title, summary, visibility, classification, personalInformation));
   }

   @Override
   public CompletionStage<Done> grant(User executor, String dataset, Authorization member, DataAssetMemberRole role) {
      return assets.grant(executor, dataset, member, role);
   }

   @Override
   public CompletionStage<Done> revoke(User executor, String dataset, Authorization member) {
      return assets.revoke(executor, dataset, member);
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String dataset, String project, String reason) {
      return assets.createDataAccessRequest(executor, dataset, project, reason);
   }

   @Override
   public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String dataset, UID request) {
      return assets.getDataAccessRequest(executor, dataset, request);
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String dataset, UID request, @Nullable Instant until, @Nullable String message) {
      return assets.grantDataAccessRequest(executor, dataset, request, until, message);
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String dataset, UID request, String reason) {
      return assets.rejectDataAccessRequest(executor, dataset, request, reason);
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String dataset, UID request, String reason) {
      return assets.updateDataAccessRequest(executor, dataset, request, reason);
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String dataset, UID request, @Nullable String reason) {
      return assets.withdrawDataAccessRequest(executor, dataset, request, reason);
   }

   @Override
   public CompletionStage<CommittedRevision> commitRevision(User executor, String dataset, UID revision, String message) {
      return datasets
         .getByName(dataset)
         .thenCompose(ds -> ds
            .getRevisions()
            .commit(executor, revision, message)
            .thenCompose(committedRevision -> {
               var desc = String.format("Analyzing dataset `%s` version `%s` ...", dataset, committedRevision.getVersion());
               return processes
                  .schedule(executor, desc, log -> {
                     log.info(desc);
                     return ds
                        .getRevisions()
                        .analyze(committedRevision.getVersion())
                        .thenApply(done -> {
                           log.info("Finished analysis of dataset `%s`, version `%s`", dataset, committedRevision.getVersion());
                           return done;
                        });
                  })
                  .thenApply(i -> committedRevision);
            }));
   }

   @Override
   public CompletionStage<Revision> createRevision(User executor, String dataset, Schema schema) {
      return datasets
         .getByName(dataset)
         .thenCompose(ds -> ds.getRevisions().createRevision(executor, schema));
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset, DatasetVersion version) {
      return datasets
         .getByName(dataset)
         .thenCompose(ds -> ds.getRevisions().download(executor, version));
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset) {
      return datasets
         .getByName(dataset)
         .thenCompose(ds -> ds
            .getRevisions()
            .getVersions()
            .thenApply(versions -> versions.stream().map(CommittedRevision::getVersion).findFirst().orElse(DatasetVersion.apply("1.0.0")))
            .thenCompose(version -> ds.getRevisions().download(executor, version)));
   }

   @Override
   public CompletionStage<Done> upload(User executor, String dataset, UID revision, Records records) {
      return datasets
         .getByName(dataset)
         .thenCompose(ds -> ds.getRevisions().upload(executor, revision, records));
   }
}
