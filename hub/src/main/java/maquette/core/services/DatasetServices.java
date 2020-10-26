package maquette.core.services;

import akka.Done;
import maquette.core.entities.datasets.model.DatasetDetails;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessToken;
import maquette.core.values.access.DataAccessTokenNarrowed;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.user.User;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DatasetServices {

   CompletionStage<DatasetDetails> createDataset(User executor, String projectName, String name, String summary, String description);

   CompletionStage<DataAccessToken> createDataAccessToken(User executor, String projectName, String datasetName, String tokenName, String description);

   CompletionStage<DataAccessRequest> createDataAccessRequest(User executor, String projectName, String datasetName, Authorization forAuthorization, String reason);

   CompletionStage<Done> deleteDataset(User executor, String projectName, String datasetName);

   CompletionStage<Done> grantDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, @Nullable Instant until, @Nullable String message);

   CompletionStage<Done> rejectDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, String reason);

   CompletionStage<Done> updateDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, String reason);

   CompletionStage<Done> withdrawDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, @Nullable  String reason);

   CompletionStage<List<DataAccessTokenNarrowed>> getDataAccessTokens(User executor, String projectName, String datasetName);

   CompletionStage<List<DataAccessRequest>> getDataAccessRequests(User executor, String projectName, String datasetName);

   CompletionStage<Optional<DataAccessRequest>> getDataAccessRequestById(User executor, String projectName, String datasetName, String accessRequestId);

}
