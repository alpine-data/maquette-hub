package maquette.core.services.data.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.validation.api.FluentValidation;
import maquette.common.validation.validators.NonEmptyStringValidator;
import maquette.common.validation.validators.NotNullValidator;
import maquette.common.validation.validators.TechnicalNameValidator;
import maquette.core.entities.data.datasets.model.Dataset;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasets.model.DatasetVersion;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.data.datasets.model.revisions.Revision;
import maquette.core.entities.data.datasets.model.tasks.Task;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.AuthorizationValidator;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.data.*;
import maquette.core.values.data.logs.DataAccessLogEntry;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.User;
import org.apache.avro.Schema;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DatasetServicesValidated implements DatasetServices {

   private final DatasetServices delegate;

   @Override
   public CompletionStage<DatasetProperties> create(
      User executor, String title, String name, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation,
      DataZone zone, @Nullable Authorization owner, @Nullable Authorization steward) {

      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("title", title, NonEmptyStringValidator.apply(3))
         .validate("name", name, TechnicalNameValidator.apply())
         .validate("summary", summary, NonEmptyStringValidator.apply(3))
         .validate("visibility", visibility, NotNullValidator.apply())
         .validate("classification", classification, NotNullValidator.apply())
         .validate("personalInformation", personalInformation, NotNullValidator.apply())
         .validate("zone", zone, NotNullValidator.apply())
         .validate("owner", owner, AuthorizationValidator.apply(UserAuthorization.class, false))
         .validate("steward", owner, AuthorizationValidator.apply(UserAuthorization.class, false))
         .checkAndFail()
         .thenCompose(done -> {
            Authorization oOwner;
            Authorization oSteward;

            if (Objects.isNull(owner)) {
               oOwner = executor.toAuthorization();
            } else {
               oOwner = owner;
            }

            if (Objects.isNull(steward)) {
               oSteward = owner;
            } else {
               oSteward = steward;
            }

            return delegate.create(
               executor, title, name, summary,
               visibility, classification, personalInformation, zone, oOwner, oSteward);
         });
   }

   @Override
   public CompletionStage<Done> remove(User executor, String dataset) {
      return FluentValidation
         .apply()
         .validate("dataset", dataset, NotNullValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.remove(executor, dataset));
   }

   @Override
   public CompletionStage<Dataset> get(User executor, String dataset) {
      return FluentValidation
         .apply()
         .validate("dataset", dataset, NotNullValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.get(executor, dataset));
   }

   @Override
   public CompletionStage<List<DatasetProperties>> list(User executor) {
      return delegate.list(executor);
   }

   @Override
   public CompletionStage<Done> update(User executor, String name, String updatedName, String title, String summary, DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation, DataZone zone) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("title", title, NonEmptyStringValidator.apply(3))
         .validate("name", name, TechnicalNameValidator.apply())
         .validate("updated", name, TechnicalNameValidator.apply())
         .validate("summary", summary, NonEmptyStringValidator.apply(3))
         .validate("visibility", visibility, NotNullValidator.apply())
         .validate("classification", classification, NotNullValidator.apply())
         .validate("personalInformation", personalInformation, NotNullValidator.apply())
         .validate("zone", zone, NotNullValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.update(executor, name, updatedName, title, summary, visibility, classification, personalInformation, zone));
   }

   @Override
   public CompletionStage<Done> approve(User executor, String dataset) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("dataset", dataset, NonEmptyStringValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.approve(executor, dataset));
   }

   @Override
   public CompletionStage<Done> deprecate(User executor, String dataset, boolean deprecate) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("dataset", dataset, NonEmptyStringValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.deprecate(executor, dataset, deprecate));
   }

   @Override
   public CompletionStage<List<Task>> getOpenTasks(User executor, String dataset) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("dataset", dataset, NonEmptyStringValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.getOpenTasks(executor, dataset));
   }

   @Override
   public CompletionStage<CommittedRevision> commitRevision(User executor, String dataset, UID revision, String message) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("dataset", dataset, NonEmptyStringValidator.apply())
         .validate("revision", revision, NotNullValidator.apply())
         .validate("message", message, NonEmptyStringValidator.apply(3))
         .checkAndFail()
         .thenCompose(done -> delegate.commitRevision(executor, dataset, revision, message));
   }

   @Override
   public CompletionStage<Revision> createRevision(User executor, String dataset, Schema schema) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("dataset", dataset, NonEmptyStringValidator.apply())
         .validate("schema", schema, NotNullValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.createRevision(executor, dataset, schema));
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset, DatasetVersion version) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("dataset", dataset, NonEmptyStringValidator.apply())
         .validate("version", version, NotNullValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.download(executor, dataset, version));
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataset) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("dataset", dataset, NonEmptyStringValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.download(executor, dataset));
   }

   @Override
   public CompletionStage<Done> upload(User executor, String dataset, UID revision, Records records) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("revision", revision, NotNullValidator.apply())
         .validate("dataset", dataset, NonEmptyStringValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.upload(executor, dataset, revision, records));
   }

   @Override
   public CompletionStage<List<DataAccessLogEntry>> getAccessLogs(User executor, String asset) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("revision", asset, NotNullValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.getAccessLogs(executor, asset));
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String asset, String project, String reason) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("asset", asset, NonEmptyStringValidator.apply())
         .validate("project", project, NonEmptyStringValidator.apply())
         .validate("reason", project, NonEmptyStringValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.createDataAccessRequest(executor, asset, project, reason));
   }

   @Override
   public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String asset, UID request) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("asset", asset, NonEmptyStringValidator.apply())
         .validate("request", request, NotNullValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.getDataAccessRequest(executor, asset, request));
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String asset, UID request, @Nullable Instant until, @Nullable String message) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("asset", asset, NonEmptyStringValidator.apply())
         .validate("request", request, NotNullValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.grantDataAccessRequest(executor, asset, request, until, message));
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String asset, UID request, String reason) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("asset", asset, NonEmptyStringValidator.apply())
         .validate("request", request, NotNullValidator.apply())
         .validate("reason", reason, NonEmptyStringValidator.apply(3))
         .checkAndFail()
         .thenCompose(done -> delegate.rejectDataAccessRequest(executor, asset, request, reason));
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String asset, UID request, String reason) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("asset", asset, NonEmptyStringValidator.apply())
         .validate("request", request, NotNullValidator.apply())
         .validate("reason", reason, NonEmptyStringValidator.apply(3))
         .checkAndFail()
         .thenCompose(done -> delegate.updateDataAccessRequest(executor, asset, request, reason));
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String asset, UID request, @Nullable String reason) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("asset", asset, NonEmptyStringValidator.apply())
         .validate("request", request, NotNullValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.withdrawDataAccessRequest(executor, asset, request, reason));
   }

   @Override
   public CompletionStage<Done> grant(User executor, String asset, Authorization member, DataAssetMemberRole role) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("asset", asset, NonEmptyStringValidator.apply())
         .validate("member", member, AuthorizationValidator.apply())
         .validate("role", role, NotNullValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.grant(executor, asset, member, role));
   }

   @Override
   public CompletionStage<Done> revoke(User executor, String asset, Authorization member) {
      return FluentValidation
         .apply()
         .validate("executor", executor, NotNullValidator.apply())
         .validate("asset", asset, NonEmptyStringValidator.apply())
         .validate("member", member, AuthorizationValidator.apply())
         .checkAndFail()
         .thenCompose(done -> delegate.revoke(executor, asset, member));
   }

}
