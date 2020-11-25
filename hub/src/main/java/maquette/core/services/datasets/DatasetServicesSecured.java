package maquette.core.services.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.model.DatasetDetails;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasets.model.DatasetVersion;
import maquette.core.entities.data.datasets.model.records.Records;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.data.datasets.model.revisions.Revision;
import maquette.core.services.projects.ProjectCompanion;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestDetails;
import maquette.core.values.access.DataAccessToken;
import maquette.core.values.access.DataAccessTokenNarrowed;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
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
public final class DatasetServicesSecured implements DatasetServices {

   private final DatasetServices delegate;

   private final ProjectCompanion projectCompanion;

   private final DatasetCompanion companion;

   @Override
   public CompletionStage<DatasetProperties> createDataset(User executor, String projectName, String title, String name, String summary, String description, DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {
      return companion
         .withAuthorization(() -> projectCompanion.isMember(executor, projectName))
         .thenCompose(d -> delegate.createDataset(executor, projectName, title, name, summary, description, visibility, classification, personalInformation));
   }

   @Override
   public CompletionStage<Done> deleteDataset(User executor, String projectName, String datasetName) {
      return companion
         .withAuthorization(() -> projectCompanion.isMember(executor, projectName))
         .thenCompose(d -> delegate.deleteDataset(executor, projectName, datasetName));
   }

   @Override
   public CompletionStage<DatasetDetails> getDataset(User executor, String projectName, String datasetName) {
      return companion
         .withAuthorization(
            () -> companion.isVisible(projectName, datasetName),
            () -> projectCompanion.isMember(executor, projectName),
            () -> companion.isOwner(executor, projectName, datasetName),
            () -> companion.isConsumer(executor, projectName, datasetName))
         .thenCompose(done -> delegate.getDataset(executor, projectName, datasetName));
   }

   @Override
   public CompletionStage<List<DatasetProperties>> getDatasets(User executor, String projectName) {
      return delegate
         .getDatasets(executor, projectName)
         .thenCompose(datasets -> Operators.allOf(datasets
            .stream()
            .map(dataset -> companion.filterAuthorized(
               dataset,
               () -> companion.isVisible(projectName, dataset.getName()),
               () -> projectCompanion.isMember(executor, projectName),
               () -> companion.isOwner(executor, projectName, dataset.getName()),
               () -> companion.isConsumer(executor, projectName, dataset.getName())))))
         .thenApply(datasets -> datasets
            .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList()));
   }

   @Override
   public CompletionStage<Done> updateDetails(User executor, String projectName, String datasetName, String name, String title, String summary, DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {
      return companion
         .withAuthorization(
            () -> companion.isOwner(executor, projectName, datasetName),
            () -> projectCompanion.isMember(executor, projectName))
         .thenCompose(d -> delegate.updateDetails(executor, projectName, datasetName, name, title, summary, visibility, classification, personalInformation));
   }

   @Override
   public CompletionStage<Done> grantDatasetOwner(User executor, String projectName, String datasetName, UserAuthorization owner) {
      return companion
         .withAuthorization(
            () -> companion.isOwner(executor, projectName, datasetName),
            () -> projectCompanion.isMember(executor, projectName))
         .thenCompose(d -> delegate.grantDatasetOwner(executor, projectName, datasetName, owner));
   }

   @Override
   public CompletionStage<Done> revokeDatasetOwner(User executor, String projectName, String datasetName, UserAuthorization owner) {
      return companion
         .withAuthorization(
            () -> companion.isOwner(executor, projectName, datasetName),
            () -> projectCompanion.isMember(executor, projectName))
         .thenCompose(d -> delegate.revokeDatasetOwner(executor, projectName, datasetName, owner));
   }

   @Override
   public CompletionStage<DataAccessToken> createDataAccessToken(User executor, String projectName, String datasetName, String origin, String tokenName, String description) {
      // subscribed
      return null;
   }

   @Override
   public CompletionStage<List<DataAccessTokenNarrowed>> getDataAccessTokens(User executor, String projectName, String datasetName) {
      // subscribed, only own tokens
      return null;
   }

   @Override
   public CompletionStage<DataAccessRequest> createDataAccessRequest(User executor, String projectName, String datasetName, String origin, String reason) {
      return companion
         .withAuthorization(
            () -> companion.isVisible(projectName, datasetName),
            () -> companion.isOwner(executor, projectName, datasetName),
            () -> projectCompanion.isMember(executor, projectName))
         .thenCompose(d -> delegate.createDataAccessRequest(executor, projectName, datasetName, origin, reason));
   }

   @Override
   public CompletionStage<List<DataAccessRequestDetails>> getDataAccessRequests(User executor, String projectName, String datasetName) {
      return Operators
         .compose(
            companion.isOwner(executor, projectName, datasetName),
            projectCompanion.isMember(executor, projectName),
            (isOwner, isMember) -> {
               if (isOwner || isMember) {
                  return delegate
                     .getDataAccessRequests(executor, projectName, datasetName)
                     .thenApply(details -> details
                        .stream()
                        .map(d -> d.withCanGrant(isOwner).withCanRequest(false))
                        .collect(Collectors.toList()));
               } else {
                  return delegate
                     .getDataAccessRequests(executor, projectName, datasetName)
                     .thenCompose(requests -> Operators.allOf(requests
                        .stream()
                        .map(request -> companion.filterAuthorized(request, () -> projectCompanion.isMember(executor, request.getOrigin().getName())))))
                     .thenApply(requests -> requests
                        .stream()
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(r -> r.withCanGrant(false).withCanRequest(true))
                        .collect(Collectors.toList()));
               }
            })
         .thenCompose(cs -> cs);
   }

   @Override
   public CompletionStage<Optional<DataAccessRequestDetails>> getDataAccessRequestById(User executor, String projectName, String datasetName, String accessRequestId) {
      return delegate
         .getDataAccessRequestById(executor, projectName, datasetName, accessRequestId)
         .thenCompose(maybeRequest -> {
            if (maybeRequest.isPresent()) {
               var request = maybeRequest.get();

               return Operators.compose(
                  companion.isOwner(executor, projectName, datasetName),
                  projectCompanion.isMember(executor, projectName),
                  projectCompanion.isMember(executor, request.getOrigin().getName()),
                  (isOwner, isMember, isOriginMember) -> {
                     if (isOwner || isMember || isOriginMember) {
                        return Optional.of(request.withCanGrant(isOwner).withCanRequest(isOriginMember));
                     } else {
                        return Optional.empty();
                     }
                  });
            } else {
               return CompletableFuture.completedFuture(maybeRequest);
            }
         });
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, @Nullable Instant until, @Nullable String message) {
      return companion
         .isAuthorized(
            () -> companion.isOwner(executor, projectName, datasetName))
         .thenCompose(done -> grantDataAccessRequest(executor, projectName, datasetName, accessRequestId, until, message));
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, String reason) {
      return companion
         .isAuthorized(
            () -> companion.isOwner(executor, projectName, datasetName))
         .thenCompose(done -> rejectDataAccessRequest(executor, projectName, datasetName, accessRequestId, reason));
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, String reason) {
      return delegate
         .getDataAccessRequestById(executor, projectName, datasetName, accessRequestId)
         .thenCompose(maybeRequest -> {
            if (maybeRequest.isPresent()) {
               var request = maybeRequest.get();

               return companion
                  .isAuthorized(
                     () -> companion.isOwner(executor, projectName, datasetName),
                     () -> projectCompanion.isMember(executor, request.getOrigin().getName()))
                  .thenCompose(ok -> delegate.updateDataAccessRequest(executor, projectName, datasetName, accessRequestId, reason));
            } else {
               return CompletableFuture.completedFuture(Done.getInstance());
            }
         });
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String projectName, String datasetName, String accessRequestId, @Nullable String reason) {
      return delegate
         .getDataAccessRequestById(executor, projectName, datasetName, accessRequestId)
         .thenCompose(maybeRequest -> {
            if (maybeRequest.isPresent()) {
               var request = maybeRequest.get();

               return companion
                  .isAuthorized(
                     () -> companion.isOwner(executor, projectName, datasetName),
                     () -> projectCompanion.isMember(executor, request.getOrigin().getName()))
                  .thenCompose(ok -> delegate.updateDataAccessRequest(executor, projectName, datasetName, accessRequestId, reason));
            } else {
               return CompletableFuture.completedFuture(Done.getInstance());
            }
         });
   }

   @Override
   public CompletionStage<CommittedRevision> commitRevision(User executor, String projectName, String datasetName, String revisionId, String message) {
      return companion
         .isAuthorized(
            () -> companion.isOwner(executor, projectName, datasetName),
            () -> projectCompanion.isMember(executor, projectName))
         .thenCompose(ok -> delegate.commitRevision(executor, projectName, datasetName, revisionId, message));
   }

   @Override
   public CompletionStage<Revision> createRevision(User executor, String projectName, String datasetName, Schema schema) {
      return companion
         .isAuthorized(
            () -> companion.isOwner(executor, projectName, datasetName),
            () -> projectCompanion.isMember(executor, projectName))
         .thenCompose(ok -> delegate.createRevision(executor, projectName, datasetName, schema));
   }

   @Override
   public CompletionStage<Records> download(User executor, String projectName, String datasetName, DatasetVersion version) {
      return companion
         .isAuthorized(
            () -> companion.isOwner(executor, projectName, datasetName),
            () -> projectCompanion.isMember(executor, projectName))
         .thenCompose(ok -> delegate.download(executor, projectName, datasetName, version));
   }

   @Override
   public CompletionStage<List<CommittedRevision>> getVersions(User executor, String projectName, String datasetName) {
      return companion
         .isAuthorized(
            () -> companion.isVisible(projectName, datasetName),
            () -> companion.isOwner(executor, projectName, datasetName),
            () -> projectCompanion.isMember(executor, projectName),
            () -> companion.isConsumer(executor, projectName, datasetName))
         .thenCompose(ok -> delegate.getVersions(executor, projectName, datasetName));
   }

   @Override
   public CompletionStage<Done> upload(User executor, String projectName, String datasetName, String revisionId, Records records) {
      return companion
         .isAuthorized(
            () -> companion.isOwner(executor, projectName, datasetName),
            () -> projectCompanion.isMember(executor, projectName))
         .thenCompose(ok -> delegate.upload(executor, projectName, datasetName, revisionId, records));
   }

}
