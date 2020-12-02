package maquette.core.services.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasets.DatasetEntity;
import maquette.core.entities.data.datasets.model.Dataset;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasets.model.DatasetVersion;
import maquette.core.entities.data.datasets.model.records.Records;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.data.datasets.model.revisions.Revision;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.ProjectEntities;
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

   private final ProjectEntities projects;

   private final DatasetCompanion companion;

   private final ProcessManager processes;

   @Override
   public CompletionStage<DatasetProperties> createDataset(User executor, String title, String name, String summary, DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {
      return datasets
         .createDataset(executor, title, name, summary, visibility, classification, personalInformation)
         .thenCompose(properties -> datasets
            .getDatasetById(properties.getId())
            .thenCompose(dataset -> dataset.members().addMember(executor, executor.toAuthorization(), DataAssetMemberRole.OWNER))
            .thenApply(done -> properties));
   }

   @Override
   public CompletionStage<Done> deleteDataset(User executor, String dataset) {
      return datasets
         .getDatasetByName(dataset)
         .thenCompose(d -> datasets.removeDataset(d.getId()));
   }

   @Override
   public CompletionStage<Dataset> getDataset(User executor, String dataset) {
      return datasets
         .getDatasetByName(dataset)
         .thenCompose(companion::mapEntityToDataset);
   }

   @Override
   public CompletionStage<List<DatasetProperties>> getDatasets(User executor) {
      return datasets.findDatasets();
   }

   @Override
   public CompletionStage<Done> updateDetails(User executor, String dataset, String updatedName, String title, String summary, DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {
      return datasets
         .getDatasetByName(dataset)
         .thenCompose(ds -> ds.updateProperties(executor, updatedName, title, summary, visibility, classification, personalInformation));
   }

   @Override
   public CompletionStage<Done> grantDatasetMember(User executor, String dataset, Authorization member, DataAssetMemberRole role) {
      return datasets
         .getDatasetByName(dataset)
         .thenCompose(ds -> ds.members().addMember(executor, member, role));
   }

   @Override
   public CompletionStage<Done> revokeDatasetMember(User executor, String dataset, Authorization member) {
      return datasets
         .getDatasetByName(dataset)
         .thenCompose(ds -> ds.members().removeMember(executor, member));
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String dataset, String project, String reason) {
      var dsCS = datasets.getDatasetByName(dataset);
      var prCS = projects.getProjectByName(project);

      return Operators
         .compose(dsCS, prCS, (ds, pr) -> ds
            .accessRequests()
            .createDataAccessRequest(executor, pr.getId(), reason))
         .thenCompose(cs -> cs);
   }

   @Override
   public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String dataset, UID request) {
      var datasetEntityCS = datasets.getDatasetByName(dataset);
      var datasetPropertiesCS = datasetEntityCS.thenCompose(DatasetEntity::getProperties);
      var accessRequestPropertiesCS = datasetEntityCS.thenCompose(ds -> ds.accessRequests().getDataAccessRequestById(request));

      return Operators
         .compose(datasetPropertiesCS, accessRequestPropertiesCS, companion::enrichDataAccessRequest)
         .thenCompose(cs -> cs);
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String dataset, UID request, @Nullable Instant until, @Nullable String message) {
      return datasets
         .getDatasetByName(dataset)
         .thenCompose(ds -> ds.accessRequests().grantDataAccessRequest(executor, request, until, message));
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String dataset, UID request, String reason) {
      return datasets
         .getDatasetByName(dataset)
         .thenCompose(ds -> ds.accessRequests().rejectDataAccessRequest(executor, request, reason));
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String dataset, UID request, String reason) {
      return datasets
         .getDatasetByName(dataset)
         .thenCompose(ds -> ds.accessRequests().updateDataAccessRequest(executor, request, reason));
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String dataset, UID request, @Nullable String reason) {
      return datasets
         .getDatasetByName(dataset)
         .thenCompose(ds -> ds.accessRequests().withdrawDataAccessRequest(executor, request, reason));
   }

   @Override
   public CompletionStage<CommittedRevision> commitRevision(User executor, String dataset, UID revision, String message) {
      return datasets
         .getDatasetByName(dataset)
         .thenCompose(ds -> ds
            .revisions()
            .commit(executor, revision, message)
            .thenCompose(committedRevision -> {
               var desc = String.format("Analyzing dataset `%s` version `%s` ...", dataset, committedRevision.getVersion());
               return processes
                  .schedule(executor, desc, log -> {
                     log.info(desc);
                     return ds
                        .revisions()
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
         .getDatasetByName(dataset)
         .thenCompose(ds -> ds.revisions().createRevision(executor, schema));
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset, DatasetVersion version) {
      return datasets
         .getDatasetByName(dataset)
         .thenCompose(ds -> ds.revisions().download(executor, version));
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset) {
      return datasets
         .getDatasetByName(dataset)
         .thenCompose(ds -> ds
            .revisions()
            .getVersions()
            .thenApply(versions -> versions.stream().map(CommittedRevision::getVersion).findFirst().orElse(DatasetVersion.apply("1.0.0")))
            .thenCompose(version -> ds.revisions().download(executor, version)));
   }

   @Override
   public CompletionStage<Done> upload(User executor, String dataset, UID revision, Records records) {
      return datasets
         .getDatasetByName(dataset)
         .thenCompose(ds -> ds.revisions().upload(executor, revision, records));
   }
}
