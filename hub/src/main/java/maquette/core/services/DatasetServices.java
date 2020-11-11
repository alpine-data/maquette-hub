package maquette.core.services;

import akka.Done;
import maquette.core.entities.datasets.model.DatasetProperties;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestDetails;
import maquette.core.values.access.DataAccessToken;
import maquette.core.values.access.DataAccessTokenNarrowed;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DatasetServices {

   CompletionStage<DatasetProperties> createDataset(
      User executor, String projectName, String title, String name, String summary, String description,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation);

   CompletionStage<DataAccessToken> createDataAccessToken(User executor, String projectName, String datasetName, String tokenName, String description);

   CompletionStage<DataAccessRequest> createDataAccessRequest(User executor, String projectName, String datasetName, String origin, String reason);

   CompletionStage<Done> deleteDataset(User executor, String projectName, String datasetName);

   CompletionStage<Done> grantDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, @Nullable Instant until, @Nullable String message);

   CompletionStage<Done> rejectDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, String reason);

   CompletionStage<Done> updateDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, String reason);

   CompletionStage<Done> withdrawDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, @Nullable  String reason);

   CompletionStage<List<DatasetProperties>> getDatasets(User executor, String projectName);

   CompletionStage<DatasetProperties> getDataset(User executor, String projectName, String datasetName);

   CompletionStage<List<DataAccessTokenNarrowed>> getDataAccessTokens(User executor, String projectName, String datasetName);

   CompletionStage<List<DataAccessRequestDetails>> getDataAccessRequests(User executor, String projectName, String datasetName);

   CompletionStage<Optional<DataAccessRequestDetails>> getDataAccessRequestById(User executor, String projectName, String datasetName, String accessRequestId);

}
