package maquette.core.services.data.datasources;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.data.datasets.model.tasks.Task;
import maquette.core.entities.data.datasources.DataSourceEntities;
import maquette.core.entities.data.datasources.model.*;
import maquette.core.entities.logs.LogEntry;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.data.*;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.User;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;


@AllArgsConstructor(staticName = "apply")
public final class DataSourceServicesSecured implements DataSourceServices {

   private final DataSourceServices delegate;

   private final DataSourceCompanion companion;

   private final DataAssetCompanion<DataSourceProperties, DataSourceEntities> assets;

   @Override
   public CompletionStage<DataSourceProperties> create(
      User executor, String title, String name, String summary,
      DataSourceDatabaseProperties properties, DataSourceAccessType type,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation,
      DataZone zone) {

      return companion
         .withAuthorization(() -> companion.isAuthenticatedUser(executor))
         .thenCompose(ok -> delegate.create(
            executor, title, name, summary, properties, type, visibility, classification, personalInformation, zone));
   }

   @Override
   public CompletionStage<Records> download(User executor, String dataSource) {
      return companion
         .withAuthorization(
            () -> assets.hasPermission(executor, dataSource, DataAssetPermissions::canConsume))
         .thenCompose(ok -> delegate.download(executor, dataSource));
   }

   @Override
   public CompletionStage<DataSource> get(User executor, String dataSource) {
      return delegate.get(executor, dataSource);
   }

   @Override
   public CompletionStage<List<DataSourceProperties>> list(User executor) {
      return delegate.list(executor);
   }

   @Override
   public CompletionStage<Done> remove(User executor, String dataSource) {
      return delegate.remove(executor, dataSource);
   }

   @Override
   public CompletionStage<Done> update(
      User executor, String name, String updatedName, String title, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation, DataZone zone) {
      return companion
         .withAuthorization(
            () -> assets.hasPermission(executor, name, DataAssetPermissions::canChangeSettings))
         .thenCompose(ok -> delegate.update(executor, name, updatedName, title, summary, visibility, classification, personalInformation, zone));
   }

   @Override
   public CompletionStage<Done> approve(User executor, String asset) {
      return delegate.approve(executor, asset);
   }

   @Override
   public CompletionStage<Done> deprecate(User executor, String asset, boolean deprecate) {
      return delegate.deprecate(executor, asset, deprecate);
   }

   @Override
   public CompletionStage<List<Task>> getOpenTasks(User executor, String asset) {
      return delegate.getOpenTasks(executor, asset);
   }

   @Override
   public CompletionStage<Done> updateDatabaseProperties(
      User executor, String dataSource, DataSourceDriver driver, String connection, String username, String password, String query,
      DataSourceAccessType accessType) {

      return companion
         .withAuthorization(
            () -> assets.hasPermission(executor, dataSource, DataAssetPermissions::canChangeSettings))
         .thenCompose(ok -> delegate.updateDatabaseProperties(executor, dataSource, driver, connection, username, password, query, accessType));
   }

   @Override
   public CompletionStage<ConnectionTestResult> test(User executor, DataSourceDriver driver, String connection, String username, String password, String query) {
      return delegate.test(executor, driver, connection, username, password, query);
   }

   @Override
   public CompletionStage<Done> grant(User executor, String asset, Authorization member, DataAssetMemberRole role) {
      return delegate.grant(executor, asset, member, role);
   }

   @Override
   public CompletionStage<Done> revoke(User executor, String asset, Authorization member) {
      return delegate.revoke(executor, asset, member);
   }

   @Override
   public CompletionStage<List<LogEntry>> getAccessLogs(User executor, String asset) {
      return delegate.getAccessLogs(executor, asset);
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String asset, String project, String reason) {
      return delegate.createDataAccessRequest(executor, asset, project, reason);
   }

   @Override
   public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String asset, UID request) {
      return delegate.getDataAccessRequest(executor, asset, request);
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String asset, UID request, @Nullable Instant until, @Nullable String message) {
      return delegate.grantDataAccessRequest(executor, asset, request, until, message);
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String asset, UID request, String reason) {
      return delegate.rejectDataAccessRequest(executor, asset, request, reason);
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String asset, UID request, String reason) {
      return delegate.updateDataAccessRequest(executor, asset, request, reason);
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String asset, UID request, @Nullable String reason) {
      return delegate.withdrawDataAccessRequest(executor, asset, request, reason);
   }

}
