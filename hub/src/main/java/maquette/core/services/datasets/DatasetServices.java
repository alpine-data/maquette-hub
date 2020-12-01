package maquette.core.services.datasets;

import akka.Done;
import maquette.core.entities.data.datasets.model.Dataset;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasets.model.DatasetVersion;
import maquette.core.entities.data.datasets.model.records.Records;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.data.datasets.model.revisions.Revision;
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

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

public interface DatasetServices {

   /*
    * General
    */
   CompletionStage<DatasetProperties> createDataset(
      User executor, String title, String name, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation);

   CompletionStage<Done> deleteDataset(User executor, String dataset);

   CompletionStage<Dataset> getDataset(User executor, String dataset);

   CompletionStage<List<DatasetProperties>> getDatasets(User executor);

   CompletionStage<Done> updateDetails(
      User executor, String name, String updatedName, String title, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation);

   /*
    * Manage access
    */
   CompletionStage<Done> grantDatasetMember(User executor, String dataset, Authorization member, DataAssetMemberRole role);

   CompletionStage<Done> revokeDatasetMember(User executor, String dataset, Authorization member);

   /*
    * Data Access Requests
    */
   CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String dataset, String project, String reason);

   CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String dataset, UID request);

   CompletionStage<Done> grantDataAccessRequest(User executor, String dataset, UID request, @Nullable Instant until, @Nullable String message);

   CompletionStage<Done> rejectDataAccessRequest(User executor, String dataset, UID request, String reason);

   CompletionStage<Done> updateDataAccessRequest(User executor, String dataset, UID request, String reason);

   CompletionStage<Done> withdrawDataAccessRequest(User executor, String dataset, UID request, @Nullable String reason);

   /*
    * Data Management
    */
   CompletionStage<CommittedRevision> commitRevision(User executor, String dataset, UID revision, String message);

   CompletionStage<Revision> createRevision(User executor, String dataset, Schema schema);

   CompletionStage<Records> download(User executor, String dataset, DatasetVersion version);

   CompletionStage<Records> download(User executor, String dataset);

   CompletionStage<Done> upload(User executor, String dataset, UID revision, Records records);

}
