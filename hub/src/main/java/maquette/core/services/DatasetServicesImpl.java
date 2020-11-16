package maquette.core.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.datasets.Dataset;
import maquette.core.entities.datasets.Datasets;
import maquette.core.entities.datasets.model.DatasetProperties;
import maquette.core.entities.datasets.model.DatasetVersion;
import maquette.core.entities.datasets.model.records.Records;
import maquette.core.entities.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.datasets.model.revisions.Revision;
import maquette.core.entities.projects.Project;
import maquette.core.entities.projects.Projects;
import maquette.core.entities.users.Users;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestDetails;
import maquette.core.values.access.DataAccessToken;
import maquette.core.values.access.DataAccessTokenNarrowed;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.exceptions.DatasetNotFoundException;
import maquette.core.values.exceptions.ProjectNotFoundException;
import maquette.core.values.user.User;
import org.apache.avro.Schema;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public class DatasetServicesImpl implements DatasetServices {

   private static final Logger LOG = LoggerFactory.getLogger(DatasetServicesImpl.class);

   private final Datasets datasets;

   private final Projects projects;

   private final Users users;

   /*
    * General
    */

   @Override
   public CompletionStage<DatasetProperties> createDataset(
      User executor, String projectName, String title, String name, String summary, String description,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {

      return withProjectByName(projectName, p -> datasets.createDataset(
         executor, p.getId(), title, name, summary, description,
         visibility, classification, personalInformation));
   }

   @Override
   public CompletionStage<Done> deleteDataset(User executor, String projectName, String datasetName) {
      return withDatasetByName(projectName, datasetName, (p, d) -> datasets.removeDataset(p.getId(), d.getId()));
   }

   @Override
   public CompletionStage<List<DatasetProperties>> getDatasets(User executor, String projectName) {
      return withProjectByName(projectName, p -> datasets.findDatasets(p.getId()));
   }

   @Override
   public CompletionStage<Done> updateDetails(
      User executor, String projectName, String datasetName, String name, String title, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {

      return withDatasetByName(projectName, datasetName, (p, d) -> d.updateDetails(
         executor, name, title, summary, visibility, classification, personalInformation));
   }

   @Override
   public CompletionStage<DatasetProperties> getDataset(User executor, String projectName, String datasetName) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.getDatasetProperties());
   }

   /*
    * Access Tokens
    */

   @Override
   public CompletionStage<DataAccessToken> createDataAccessToken(User executor, String projectName, String datasetName, String origin, String tokenName, String description) {
      return withProjectByName(origin, originProject -> withDatasetByName(
         projectName, datasetName, (p, d) -> d.accessTokens().createDataAccessToken(executor, originProject.getId(), tokenName, description)));
   }

   @Override
   public CompletionStage<List<DataAccessTokenNarrowed>> getDataAccessTokens(User executor, String projectName, String datasetName) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.accessTokens().getDataAccessTokens())
         .thenApply(tokens -> tokens.stream().map(DataAccessToken::toNarrowed).collect(Collectors.toList()));
   }

   /*
    * Access Requests
    */

   @Override
   public CompletionStage<DataAccessRequest> createDataAccessRequest(User executor, String projectName, String datasetName, String origin, String reason) {
      return withProjectByName(origin, target ->
         withDatasetByName(projectName, datasetName, (p, d) -> d.accessRequests().createDataAccessRequest(executor, target.getId(), reason)));
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, @Nullable Instant until, @Nullable String message) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.accessRequests().grantDataAccessRequest(executor, accessRequestId, until, message));
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, String reason) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.accessRequests().rejectDataAccessRequest(executor, accessRequestId, reason));
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, String reason) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.accessRequests().updateDataAccessRequest(executor, accessRequestId, reason));
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, @Nullable String reason) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.accessRequests().withdrawDataAccessRequest(executor, accessRequestId, reason));
   }

   /*
    * Data Management
    */

   @Override
   public CompletionStage<CommittedRevision> commitRevision(User executor, String projectName, String datasetName, String revisionId, String message) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.revisions().commit(executor, revisionId, message));
   }

   @Override
   public CompletionStage<Revision> createRevision(User executor, String projectName, String datasetName, Schema schema) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.revisions().createRevision(executor, schema));
   }

   @Override
   public CompletionStage<Records> download(User executor, String projectName, String datasetName, DatasetVersion version) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.revisions().download(executor, version));
   }

   @Override
   public CompletionStage<List<CommittedRevision>> getVersions(User executor, String projectName, String datasetName) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.revisions().getVersions());
   }

   @Override
   public CompletionStage<Done> upload(User executor, String projectName, String datasetName, String revisionId, Records records) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.revisions().upload(executor, revisionId, records));
   }

   @Override
   public CompletionStage<List<DataAccessRequestDetails>> getDataAccessRequests(User executor, String projectName, String datasetName) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.accessRequests().getDataAccessRequests())
         .thenCompose(requests -> Operators.allOf(requests
            .stream()
            .map(this::mapDataAccessRequestToSummary)
            .collect(Collectors.toList())))
         .thenApply(requests -> requests
            .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList()));
   }

   @Override
   public CompletionStage<Optional<DataAccessRequestDetails>> getDataAccessRequestById(User executor, String projectName, String datasetName, String accessRequestId) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.accessRequests().getDataAccessRequestById(accessRequestId))
         .thenCompose(request -> {
            if (request.isPresent()) {
               return mapDataAccessRequestToSummary(request.get());
            } else {
               return CompletableFuture.completedFuture(Optional.empty());
            }
         });
   }

   /*
    * Helper functions
    */

   private CompletionStage<Optional<DataAccessRequestDetails>> mapDataAccessRequestToSummary(DataAccessRequest request) {
      return projects
         .findProjectById(request.getOrigin())
         .thenCompose(maybeProject -> {
            if (maybeProject.isPresent()) {
               return maybeProject.get().getProperties().thenApply(p -> Optional.of(DataAccessRequestDetails.apply(
                  request.getId(), request.getCreated(), p, request.getEvents(), request.getStatus(), true, true)));
            } else {
               LOG.warn("Data Access Request {} is linked to non-existing project.", request);
               return CompletableFuture.completedFuture(Optional.empty());
            }
         });
   }

   private <T> CompletionStage<T> withProjectByName(String projectName, Function<Project, CompletionStage<T>> func) {
      return projects
         .findProjectByName(projectName)
         .thenCompose(maybeProject -> {
            if (maybeProject.isPresent()) {
               return func.apply(maybeProject.get());
            } else {
               throw ProjectNotFoundException.applyFromName(projectName);
            }
         });
   }

   private <T> CompletionStage<T> withDatasetByName(String projectName, String datasetName, BiFunction<Project, Dataset, CompletionStage<T>> func) {
      return withProjectByName(projectName, project -> datasets
         .findDatasetByName(project.getId(), datasetName)
         .thenCompose(maybeDataset -> {
            if (maybeDataset.isPresent()) {
               return func.apply(project, maybeDataset.get());
            } else {
               throw DatasetNotFoundException.applyFromName(projectName, datasetName);
            }
         }));
   }

}
