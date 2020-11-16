package maquette.core.services;

import akka.Done;
import maquette.core.entities.datasets.model.DatasetProperties;
import maquette.core.entities.datasets.model.DatasetVersion;
import maquette.core.entities.datasets.model.records.Records;
import maquette.core.entities.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.datasets.model.revisions.Revision;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestDetails;
import maquette.core.values.access.DataAccessToken;
import maquette.core.values.access.DataAccessTokenNarrowed;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;
import org.apache.avro.Schema;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DatasetServices {

   /*
    * General
    */
   CompletionStage<DatasetProperties> createDataset(
      User executor, String projectName, String title, String name, String summary, String description,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation);

   CompletionStage<Done> deleteDataset(User executor, String projectName, String datasetName);

   CompletionStage<DatasetProperties> getDataset(User executor, String projectName, String datasetName);

   CompletionStage<List<DatasetProperties>> getDatasets(User executor, String projectName);

   CompletionStage<Done> updateDetails(
      User executor, String projectName, String datasetName, String name, String title, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation);

   /*
    * Data Access Tokens
    */
   CompletionStage<DataAccessToken> createDataAccessToken(User executor, String projectName, String datasetName, String origin, String tokenName, String description);

   CompletionStage<List<DataAccessTokenNarrowed>> getDataAccessTokens(User executor, String projectName, String datasetName);

   /*
    * Data Access Requests
    */
   CompletionStage<DataAccessRequest> createDataAccessRequest(User executor, String projectName, String datasetName, String origin, String reason);

   CompletionStage<List<DataAccessRequestDetails>> getDataAccessRequests(User executor, String projectName, String datasetName);

   CompletionStage<Optional<DataAccessRequestDetails>> getDataAccessRequestById(User executor, String projectName, String datasetName, String accessRequestId);

   CompletionStage<Done> grantDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, @Nullable Instant until, @Nullable String message);

   CompletionStage<Done> rejectDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, String reason);

   CompletionStage<Done> updateDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, String reason);

   CompletionStage<Done> withdrawDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, @Nullable  String reason);

   /*
    * Data Management
    */
   CompletionStage<CommittedRevision> commitRevision(User executor, String projectName, String datasetName, String revisionId, String message);

   CompletionStage<Revision> createRevision(User executor, String projectName, String datasetName, Schema schema);

   CompletionStage<Records> download(User executor, String projectName, String datasetName, DatasetVersion version);

   CompletionStage<List<CommittedRevision>> getVersions(User executor, String projectName, String datasetName);

   CompletionStage<Done> upload(User executor, String projectName, String datasetName, String revisionId, Records records);

}
