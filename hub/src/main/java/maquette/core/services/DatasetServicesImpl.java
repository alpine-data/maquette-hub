package maquette.core.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.datasets.Dataset;
import maquette.core.entities.datasets.Datasets;
import maquette.core.entities.datasets.model.DatasetDetails;
import maquette.core.entities.projects.Project;
import maquette.core.entities.projects.Projects;
import maquette.core.entities.users.Users;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessToken;
import maquette.core.values.access.DataAccessTokenNarrowed;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.exceptions.DatasetNotFoundException;
import maquette.core.values.exceptions.ProjectNotFoundException;
import maquette.core.values.user.User;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public class DatasetServicesImpl implements DatasetServices {

   private final Datasets datasets;

   private final Projects projects;

   private final Users users;

   @Override
   public CompletionStage<DatasetDetails> createDataset(User executor, String projectName, String name, String summary, String description) {
      return withProjectByName(projectName, p -> datasets.createDataset(executor, p.getId(), name, summary, description));
   }

   @Override
   public CompletionStage<DataAccessToken> createDataAccessToken(User executor, String projectName, String datasetName, String tokenName, String description) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.createDataAccessToken(executor, tokenName, description));
   }

   @Override
   public CompletionStage<DataAccessRequest> createDataAccessRequest(User executor, String projectName, String datasetName, Authorization forAuthorization, String reason) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.createDataAccessRequest(executor, forAuthorization, reason));
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
   public CompletionStage<List<DataAccessTokenNarrowed>> getDataAccessTokens(User executor, String projectName, String datasetName) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.getDataAccessTokens())
         .thenApply(tokens -> tokens.stream().map(DataAccessToken::toNarrowed).collect(Collectors.toList()));
   }

   @Override
   public CompletionStage<List<DataAccessRequest>> getDataAccessRequests(User executor, String projectName, String datasetName) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.getDataAccessRequests());
   }

   @Override
   public CompletionStage<Optional<DataAccessRequest>> getDataAccessRequestById(User executor, String projectName, String datasetName, String accessRequestId) {
      return withDatasetByName(projectName, datasetName, (p, d) -> d.getDataAccessRequestById(accessRequestId));
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
