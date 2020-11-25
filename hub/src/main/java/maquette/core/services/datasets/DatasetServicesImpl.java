package maquette.core.services.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.Datasets;
import maquette.core.entities.data.datasets.model.DatasetDetails;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasets.model.DatasetVersion;
import maquette.core.entities.data.datasets.model.records.Records;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.data.datasets.model.revisions.Revision;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestDetails;
import maquette.core.values.access.DataAccessToken;
import maquette.core.values.access.DataAccessTokenNarrowed;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import org.apache.avro.Schema;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DatasetServicesImpl implements DatasetServices {

   private final Datasets datasets;

   private final DatasetCompanion companion;

   /*
    * General
    */

   @Override
   public CompletionStage<DatasetProperties> createDataset(
      User executor, String projectName, String title, String name, String summary, String description,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {

      return companion.withProjectByName(projectName, p -> datasets
         .createDataset(
            executor, p.getId(), title, name, summary, description,
            visibility, classification, personalInformation)
         .thenCompose(properties -> datasets.getDatasetById(p.getId(), properties.getId()))
         .thenCompose(d -> {
            if (executor instanceof AuthenticatedUser) {
               return d
                  .addOwner(executor, UserAuthorization.apply(((AuthenticatedUser) executor).getId()))
                  .thenCompose(done -> d.getProperties());
            } else {
               return d.getProperties();
            }
         }));
   }

   @Override
   public CompletionStage<Done> deleteDataset(User executor, String projectName, String datasetName) {
      return companion.withDatasetByName(projectName, datasetName, (p, d) -> datasets.removeDataset(p.getId(), d.getId()));
   }

   @Override
   public CompletionStage<List<DatasetProperties>> getDatasets(User executor, String projectName) {
      return companion.withProjectByName(projectName, p -> datasets.findDatasets(p.getId()));
   }

   @Override
   public CompletionStage<Done> updateDetails(
      User executor, String projectName, String datasetName, String name, String title, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {

      return companion.withDatasetByName(projectName, datasetName, (p, d) -> d.updateDetails(
         executor, name, title, summary, visibility, classification, personalInformation));
   }

   /*
    * Manage access
    */

   @Override
   public CompletionStage<Done> grantDatasetOwner(User executor, String projectName, String datasetName, UserAuthorization owner) {
      return companion.withDatasetByName(projectName, datasetName, (project, dataset) -> dataset.addOwner(executor, owner));
   }

   @Override
   public CompletionStage<Done> revokeDatasetOwner(User executor, String projectName, String datasetName, UserAuthorization owner) {
      return companion.withDatasetByName(projectName, datasetName, (project, dataset) -> dataset.removeOwner(executor, owner));
   }

   @Override
   public CompletionStage<DatasetDetails> getDataset(User executor, String projectName, String datasetName) {
      return companion.withDatasetByName(projectName, datasetName, (p, d) -> d.getDatasetDetails());
   }

   /*
    * Access Tokens
    */

   @Override
   public CompletionStage<DataAccessToken> createDataAccessToken(User executor, String projectName, String datasetName, String origin, String tokenName, String description) {
      return companion.withProjectByName(origin, originProject -> companion.withDatasetByName(
         projectName, datasetName, (p, d) -> d.accessTokens().createDataAccessToken(executor, originProject.getId(), tokenName, description)));
   }

   @Override
   public CompletionStage<List<DataAccessTokenNarrowed>> getDataAccessTokens(User executor, String projectName, String datasetName) {
      return companion.withDatasetByName(projectName, datasetName, (p, d) -> d.accessTokens().getDataAccessTokens())
         .thenApply(tokens -> tokens.stream().map(DataAccessToken::toNarrowed).collect(Collectors.toList()));
   }

   /*
    * Access Requests
    */

   @Override
   public CompletionStage<DataAccessRequest> createDataAccessRequest(User executor, String projectName, String datasetName, String origin, String reason) {
      return companion.withProjectByName(origin, target ->
         companion.withDatasetByName(projectName, datasetName, (p, d) -> d.accessRequests().createDataAccessRequest(executor, target.getId(), reason)));
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, @Nullable Instant until, @Nullable String message) {
      return companion.withDatasetByName(projectName, datasetName, (p, d) -> d.accessRequests().grantDataAccessRequest(executor, accessRequestId, until, message));
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, String reason) {
      return companion.withDatasetByName(projectName, datasetName, (p, d) -> d.accessRequests().rejectDataAccessRequest(executor, accessRequestId, reason));
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, String reason) {
      return companion.withDatasetByName(projectName, datasetName, (p, d) -> d.accessRequests().updateDataAccessRequest(executor, accessRequestId, reason));
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, @Nullable String reason) {
      return companion.withDatasetByName(projectName, datasetName, (p, d) -> d.accessRequests().withdrawDataAccessRequest(executor, accessRequestId, reason));
   }

   /*
    * Data Management
    */

   @Override
   public CompletionStage<CommittedRevision> commitRevision(User executor, String projectName, String datasetName, String revisionId, String message) {
      return companion.withDatasetByName(projectName, datasetName, (p, d) -> d.revisions().commit(executor, revisionId, message));
   }

   @Override
   public CompletionStage<Revision> createRevision(User executor, String projectName, String datasetName, Schema schema) {
      return companion.withDatasetByName(projectName, datasetName, (p, d) -> d.revisions().createRevision(executor, schema));
   }

   @Override
   public CompletionStage<Records> download(User executor, String projectName, String datasetName, DatasetVersion version) {
      return companion.withDatasetByName(projectName, datasetName, (p, d) -> d.revisions().download(executor, version));
   }

   @Override
   public CompletionStage<List<CommittedRevision>> getVersions(User executor, String projectName, String datasetName) {
      return companion.withDatasetByName(projectName, datasetName, (p, d) -> d.revisions().getVersions());
   }

   @Override
   public CompletionStage<Done> upload(User executor, String projectName, String datasetName, String revisionId, Records records) {
      return companion.withDatasetByName(projectName, datasetName, (p, d) -> d.revisions().upload(executor, revisionId, records));
   }

   @Override
   public CompletionStage<List<DataAccessRequestDetails>> getDataAccessRequests(User executor, String projectName, String datasetName) {
      return companion.withDatasetByName(projectName, datasetName, (p, d) -> d
         .accessRequests()
         .getDataAccessRequests()
         .thenCompose(requests -> Operators.allOf(requests
            .stream()
            .map(r -> companion.mapDataAccessRequestToDetails(p, d, r))
            .collect(Collectors.toList()))))
         .thenApply(requests -> requests
            .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList()));
   }

   @Override
   public CompletionStage<Optional<DataAccessRequestDetails>> getDataAccessRequestById(User executor, String projectName, String datasetName, String accessRequestId) {
      return companion.withDatasetByName(projectName, datasetName, (p, d) -> d
         .accessRequests()
         .getDataAccessRequestById(accessRequestId)
         .thenCompose(request -> {
            if (request.isPresent()) {
               return companion.mapDataAccessRequestToDetails(p, d, request.get());
            } else {
               return CompletableFuture.completedFuture(Optional.empty());
            }
         }));
   }

}
