package maquette.core.services.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
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
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public class DatasetServicesSecured implements DatasetServices {

   private final DatasetServices delegate;

   private final DatasetCompanion companion;

   @Override
   public CompletionStage<DatasetProperties> createDataset(User executor, String title, String name, String summary, DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {
      return companion
         .withAuthorization(() -> companion.isAuthenticatedUser(executor))
         .thenCompose(ok -> delegate.createDataset(executor, title, name, summary, visibility, classification, personalInformation));
   }

   @Override
   public CompletionStage<Done> deleteDataset(User executor, String dataset) {
      return companion
         .withAuthorization(() -> companion.isMember(executor, dataset, DataAssetMemberRole.OWNER))
         .thenCompose(ok -> delegate.deleteDataset(executor, dataset));
   }

   @Override
   public CompletionStage<Dataset> getDataset(User executor, String dataset) {
      var isMemberCS = companion.isMember(executor, dataset);
      var isSubscribedCS = companion.isSubscribedConsumer(executor, dataset);
      return companion
         .withAuthorization(
            () -> companion.isVisible(dataset),
            () -> isMemberCS,
            () -> isSubscribedCS)
         .thenCompose(ok -> delegate.getDataset(executor, dataset))
         .thenCompose(ds -> {
            var isOwnerCS = companion.isMember(executor, dataset, DataAssetMemberRole.OWNER);

            return Operators
               .compose(isMemberCS, isSubscribedCS, isOwnerCS, (isMember, isSubscribed, isOwner) -> {
                  if (isOwner) {
                     var requests = ds
                        .getAccessRequests()
                        .stream()
                        .map(request -> request.withCanGrant(true))
                        .collect(Collectors.toList());

                     return CompletableFuture.completedFuture(ds.withAccessRequests(requests));
                  } else {
                     return Operators
                        .allOf(ds
                           .getAccessRequests()
                           .stream()
                           .map(request -> companion.filterRequester(executor, ds.getName(), request.getId(), request)))
                        .thenApply(all -> all
                           .stream()
                           .filter(Optional::isPresent)
                           .map(Optional::get)
                           .map(request -> request.withCanRequest(true))
                           .collect(Collectors.toList()))
                        .thenApply(ds::withAccessRequests);
                  }
               })
               .thenCompose(cs -> cs);
         });
   }

   @Override
   public CompletionStage<List<DatasetProperties>> getDatasets(User executor) {
      return delegate
         .getDatasets(executor)
         .thenApply(datasets -> datasets
            .stream()
            .map(ds -> companion.filterAuthorized(
               ds,
               () -> companion.isVisible(ds.getName()),
               () -> companion.isMember(executor, ds.getName()),
               () -> companion.isSubscribedConsumer(executor, ds.getName()))))
         .thenCompose(Operators::allOf)
         .thenApply(datasets -> datasets
            .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList()));
   }

   @Override
   public CompletionStage<Done> updateDetails(User executor, String name, String updatedName, String title, String summary, DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {
      return companion
         .withAuthorization(
            () -> companion.isMember(executor, name, DataAssetMemberRole.OWNER))
         .thenCompose(ok -> delegate.updateDetails(executor, name, updatedName, title, summary, visibility, classification, personalInformation));
   }

   @Override
   public CompletionStage<Done> grantDatasetMember(User executor, String dataset, Authorization member, DataAssetMemberRole role) {
      return companion
         .withAuthorization(
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.OWNER))
         .thenCompose(ok -> delegate.grantDatasetMember(executor, dataset, member, role));
   }

   @Override
   public CompletionStage<Done> revokeDatasetMember(User executor, String dataset, Authorization member) {
      return companion
         .withAuthorization(
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.OWNER))
         .thenCompose(ok -> delegate.revokeDatasetMember(executor, dataset, member));
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String dataset, String project, String reason) {
      return companion
         .withAuthorization(
            () -> companion.isVisible(dataset),
            () -> companion.isMember(executor, dataset))
         .thenCompose(ok -> delegate.createDataAccessRequest(executor, dataset, project, reason));
   }

   @Override
   public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String dataset, UID request) {
      return companion
         .withAuthorization(
            () -> companion.isMember(executor, dataset),
            () -> companion.isRequester(executor, dataset, request))
         .thenCompose(ok -> delegate.getDataAccessRequest(executor, dataset, request));
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String dataset, UID request, @Nullable Instant until, @Nullable String message) {
      return companion
         .withAuthorization(
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.OWNER))
         .thenCompose(ok -> delegate.grantDataAccessRequest(executor, dataset, request, until, message));
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String dataset, UID request, String reason) {
      return companion
         .withAuthorization(
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.OWNER))
         .thenCompose(ok -> delegate.rejectDataAccessRequest(executor, dataset, request, reason));
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String dataset, UID request, String reason) {
      return companion
         .withAuthorization(
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.OWNER),
            () -> companion.isRequester(executor, dataset, request))
         .thenCompose(ok -> delegate.updateDataAccessRequest(executor, dataset, request, reason));
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String dataset, UID request, @Nullable String reason) {
      return companion
         .withAuthorization(
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.OWNER),
            () -> companion.isRequester(executor, dataset, request))
         .thenCompose(ok -> delegate.withdrawDataAccessRequest(executor, dataset, request, reason));
   }

   @Override
   public CompletionStage<CommittedRevision> commitRevision(User executor, String dataset, UID revision, String message) {
      return companion
         .withAuthorization(
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.OWNER),
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.MEMBER),
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.PRODUCER))
         .thenCompose(ok -> delegate.commitRevision(executor, dataset, revision, message));
   }

   @Override
   public CompletionStage<Revision> createRevision(User executor, String dataset, Schema schema) {
      return companion
         .withAuthorization(
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.OWNER),
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.MEMBER),
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.PRODUCER))
         .thenCompose(ok -> delegate.createRevision(executor, dataset, schema));
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset, DatasetVersion version) {
      return companion
         .withAuthorization(
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.OWNER),
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.MEMBER),
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.CONSUMER))
         .thenCompose(ok -> delegate.download(executor, dataset, version));
   }

   @Override
   public CompletionStage<Done> upload(User executor, String dataset, UID revision, Records records) {
      return companion
         .withAuthorization(
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.OWNER),
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.MEMBER),
            () -> companion.isMember(executor, dataset, DataAssetMemberRole.PRODUCER))
         .thenCompose(ok -> delegate.upload(executor, dataset, revision, records));
   }
}
