package maquette.core.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.datasets.Dataset;
import maquette.core.entities.datasets.Datasets;
import maquette.core.entities.datasets.model.DatasetProperties;
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

   @Override
   public CompletionStage<DatasetProperties> createDataset(
      User executor, String projectName, String title, String name, String summary, String description,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {

      return withProjectByName(projectName, p -> datasets.createDataset(
         executor, p.getId(), title, name, summary, description,
         visibility, classification, personalInformation));
   }

   @Override
   public CompletionStage<DataAccessToken> createDataAccessToken(User executor, String projectName, String datasetName, String tokenName, String description) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.createDataAccessToken(executor, tokenName, description));
   }

   @Override
   public CompletionStage<DataAccessRequest> createDataAccessRequest(User executor, String projectName, String datasetName, String origin, String reason) {
      return withProjectByName(origin, target ->
         withDatasetByName(projectName, datasetName, (p, d) -> d.createDataAccessRequest(executor, target.getId(), reason)));
   }

   @Override
   public CompletionStage<Done> deleteDataset(User executor, String projectName, String datasetName) {
      return withDatasetByName(projectName, datasetName, (p, d) -> datasets.removeDataset(p.getId(), d.getId()));
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, @Nullable Instant until, @Nullable String message) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.grantDataAccessRequest(executor, accessRequestId, until, message));
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, String reason) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.rejectDataAccessRequest(executor, accessRequestId, reason));
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, String reason) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.updateDataAccessRequest(executor, accessRequestId, reason));
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, @Nullable String reason) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.withdrawDataAccessRequest(executor, accessRequestId, reason));
   }

   @Override
   public CompletionStage<List<DatasetProperties>> getDatasets(User executor, String projectName) {
      return withProjectByName(projectName, p -> datasets.findDatasets(p.getId()));
   }

   @Override
   public CompletionStage<DatasetProperties> getDataset(User executor, String projectName, String datasetName) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.getDatasetProperties());
   }

   @Override
   public CompletionStage<List<DataAccessTokenNarrowed>> getDataAccessTokens(User executor, String projectName, String datasetName) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.getDataAccessTokens())
         .thenApply(tokens -> tokens.stream().map(DataAccessToken::toNarrowed).collect(Collectors.toList()));
   }

   @Override
   public CompletionStage<List<DataAccessRequestDetails>> getDataAccessRequests(User executor, String projectName, String datasetName) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.getDataAccessRequests())
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
      return withDatasetByName(projectName, datasetName, (p, d) -> d.getDataAccessRequestById(accessRequestId))
         .thenCompose(request -> {
            if (request.isPresent()) {
               return mapDataAccessRequestToSummary(request.get());
            } else {
               return CompletableFuture.completedFuture(Optional.empty());
            }
         });
   }

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
