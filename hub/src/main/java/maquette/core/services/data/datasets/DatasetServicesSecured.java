package maquette.core.services.data.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasets.model.Dataset;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasets.model.DatasetVersion;
import maquette.core.values.data.logs.DataAccessLogEntry;
import maquette.core.values.data.records.Records;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.data.datasets.model.revisions.Revision;
import maquette.core.services.data.DataAssetCompanion;
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
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DatasetServicesSecured implements DatasetServices {

   private final DatasetServices delegate;

   private final DatasetCompanion companion;

   private final DataAssetCompanion<DatasetProperties, DatasetEntities> assets;

   @Override
   public CompletionStage<DatasetProperties> create(User executor, String title, String name, String summary, DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {
      return companion
         .withAuthorization(() -> companion.isAuthenticatedUser(executor))
         .thenCompose(ok -> delegate.create(executor, title, name, summary, visibility, classification, personalInformation));
   }

   @Override
   public CompletionStage<Done> remove(User executor, String dataset) {
      return delegate.remove(executor, dataset);
   }

   @Override
   public CompletionStage<Dataset> get(User executor, String dataset) {
      return delegate.get(executor, dataset);
   }

   @Override
   public CompletionStage<List<DatasetProperties>> list(User executor) {
      return delegate.list(executor);
   }

   @Override
   public CompletionStage<Done> update(User executor, String name, String updatedName, String title, String summary, DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {
      return companion
         .withAuthorization(
            () -> assets.isMember(executor, name, DataAssetMemberRole.OWNER))
         .thenCompose(ok -> delegate.update(executor, name, updatedName, title, summary, visibility, classification, personalInformation));
   }

   @Override
   public CompletionStage<Done> grant(User executor, String dataset, Authorization member, DataAssetMemberRole role) {
      return delegate.grant(executor, dataset, member, role);
   }

   @Override
   public CompletionStage<Done> revoke(User executor, String dataset, Authorization member) {
      return delegate.revoke(executor, dataset, member);
   }

   @Override
   public CompletionStage<List<DataAccessLogEntry>> getAccessLogs(User executor, String asset) {
      return delegate.getAccessLogs(executor, asset);
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String dataset, String project, String reason) {
      return delegate.createDataAccessRequest(executor, dataset, project, reason);
   }

   @Override
   public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String dataset, UID request) {
      return delegate.getDataAccessRequest(executor, dataset, request);
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String dataset, UID request, @Nullable Instant until, @Nullable String message) {
      return delegate.grantDataAccessRequest(executor, dataset, request, until, message);
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String dataset, UID request, String reason) {
      return delegate.rejectDataAccessRequest(executor, dataset, request, reason);
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String dataset, UID request, String reason) {
      return delegate.updateDataAccessRequest(executor, dataset, request, reason);
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String dataset, UID request, @Nullable String reason) {
      return delegate.withdrawDataAccessRequest(executor, dataset, request, reason);
   }

   @Override
   public CompletionStage<CommittedRevision> commitRevision(User executor, String dataset, UID revision, String message) {
      return companion
         .withAuthorization(
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.OWNER),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.MEMBER),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.PRODUCER))
         .thenCompose(ok -> delegate.commitRevision(executor, dataset, revision, message));
   }

   @Override
   public CompletionStage<Revision> createRevision(User executor, String dataset, Schema schema) {
      return companion
         .withAuthorization(
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.OWNER),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.MEMBER),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.PRODUCER))
         .thenCompose(ok -> delegate.createRevision(executor, dataset, schema));
   }

   @Override
   public CompletionStage<Records> download(User executor, UID project, String dataset, DatasetVersion version) {
      return companion
         .withAuthorization(
            () -> assets.isSubscribedConsumer(executor, dataset, project),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.OWNER),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.MEMBER),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.CONSUMER))
         .thenCompose(ok -> delegate.download(executor, project, dataset, version));
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset, DatasetVersion version) {
      return companion
         .withAuthorization(
            () -> assets.isSubscribedConsumer(executor, dataset),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.OWNER),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.MEMBER),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.CONSUMER))
         .thenCompose(ok -> delegate.download(executor, dataset, version));
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset) {
      return companion
         .withAuthorization(
            () -> assets.isSubscribedConsumer(executor, dataset),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.OWNER),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.MEMBER),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.CONSUMER))
         .thenCompose(ok -> delegate.download(executor, dataset));
   }

   @Override
   public CompletionStage<Records> download(User executor, UID project, String dataset) {
      return companion
         .withAuthorization(
            () -> assets.isSubscribedConsumer(executor, dataset, project),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.OWNER),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.MEMBER),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.CONSUMER))
         .thenCompose(ok -> delegate.download(executor, project, dataset));
   }

   @Override
   public CompletionStage<Done> upload(User executor, String dataset, UID revision, Records records) {
      return companion
         .withAuthorization(
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.OWNER),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.MEMBER),
            () -> assets.isMember(executor, dataset, DataAssetMemberRole.PRODUCER))
         .thenCompose(ok -> delegate.upload(executor, dataset, revision, records));
   }
}
