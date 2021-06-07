package maquette.core.services.data;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.model.DataAsset;
import maquette.core.entities.data.model.DataAssetMetadata;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.entities.data.model.tasks.Task;
import maquette.core.entities.logs.LogEntry;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.data.DataAssetPermissions;
import maquette.core.values.user.User;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DataAssetServicesSecured implements DataAssetServices {

   private final DataAssetServices delegate;

   private final DataAssetCompanion comp;

   @Override
   public CompletionStage<DataAssetProperties> create(User executor, String type, DataAssetMetadata metadata, Authorization owner, Authorization steward, @Nullable Object customSettings) {
      return comp
         .withAuthorization(() -> comp.isAuthenticatedUser(executor))
         .thenCompose(ok -> delegate.create(executor, type, metadata, owner, steward, customSettings));
   }

   @Override
   public CompletionStage<DataAsset> get(User executor, String name) {
      var isMemberCS = comp.isMember(executor, name);
      var isSubscribedCS = comp.isSubscribedConsumer(executor, name);

      return comp
         .withAuthorization(
            executor::isSystemUserCS,
            () -> comp.isVisible(name),
            () -> isMemberCS,
            () -> isSubscribedCS)
         .thenCompose(ok -> delegate.get(executor, name))
         .thenCompose(entity -> {
            var isOwnerCS = comp.isMember(executor, name, DataAssetMemberRole.OWNER);

            return Operators
               .compose(isMemberCS, isSubscribedCS, isOwnerCS, (isMember, isSubscribed, isOwner) -> {
                  if (isOwner) {
                     var requests = entity
                        .getAccessRequests()
                        .stream()
                        .map(request -> request.withCanGrant(true))
                        .collect(Collectors.toList());

                     return CompletableFuture.completedFuture(entity.withAccessRequests(requests));
                  } else {
                     return Operators
                        .allOf(entity
                           .getAccessRequests()
                           .stream()
                           .map(request -> comp.filterRequester(
                              executor,
                              entity.getProperties().getMetadata().getName(),
                              request.getId(),
                              request)))
                        .thenApply(all -> all
                           .stream()
                           .filter(Optional::isPresent)
                           .map(Optional::get)
                           .map(request -> request.withCanRequest(true))
                           .collect(Collectors.toList()))
                        .thenApply(entity::withAccessRequests);
                  }
               })
               .thenCompose(cs -> cs);
         });
   }

   @Override
   public CompletionStage<List<DataAssetProperties>> list(User executor) {
      return delegate
         .list(executor)
         .thenApply(datasets -> datasets
            .stream()
            .map(entity -> comp.filterAuthorized(
               entity,
               executor::isSystemUserCS,
               () -> comp.isVisible(entity.getMetadata().getName()),
               () -> comp.isMember(executor, entity.getMetadata().getName()),
               () -> comp.isSubscribedConsumer(executor, entity.getMetadata().getName()))))
         .thenCompose(Operators::allOf)
         .thenApply(datasets -> datasets
            .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList()));
   }

   @Override
   public CompletionStage<Done> approve(User executor, String name) {
      return comp
         .withAuthorization(
            () -> comp.hasPermission(executor, name, DataAssetPermissions::canManageState))
         .thenCompose(ok -> delegate.approve(executor, name));
   }

   @Override
   public CompletionStage<Done> deprecate(User executor, String name, boolean deprecate) {
      return comp
         .withAuthorization(
            () -> comp.hasPermission(executor, name, DataAssetPermissions::canManageState))
         .thenCompose(ok -> delegate.deprecate(executor, name, deprecate));
   }

   @Override
   public CompletionStage<Done> update(User executor, String name, DataAssetMetadata metadata) {
      return comp
         .withAuthorization(
            () -> comp.hasPermission(executor, name, DataAssetPermissions::canChangeSettings))
         .thenCompose(ok -> delegate.update(executor, name, metadata));
   }

   @Override
   public CompletionStage<Done> updateCustomSettings(User executor, String name, Object customSettings) {
      return comp
         .withAuthorization(
            () -> comp.hasPermission(executor, name, DataAssetPermissions::canChangeSettings))
         .thenCompose(ok -> delegate.updateCustomSettings(executor, name, customSettings));
   }

   @Override
   public CompletionStage<Done> remove(User executor, String name) {
      return comp
         .withAuthorization(
            executor::isSystemUserCS,
            () -> comp.hasPermission(executor, name, DataAssetPermissions::canChangeSettings))
         .thenCompose(ok -> delegate.remove(executor, name));
   }

   @Override
   public CompletionStage<List<LogEntry>> getAccessLogs(User executor, String name) {
      return comp
         .withAuthorization(
            () -> comp.hasPermission(executor, name, DataAssetPermissions::canReviewLogs))
         .thenCompose(ok -> delegate.getAccessLogs(executor, name));
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String name, String project, String reason) {
      return comp
         .withAuthorization(
            () -> comp.isVisible(name),
            () -> comp.hasPermission(executor, name, DataAssetPermissions::canConsume))
         .thenCompose(ok -> delegate.createDataAccessRequest(executor, name, project, reason));
   }

   @Override
   public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String name, UID request) {
      return comp
         .withAuthorization(
            executor::isSystemUserCS,
            () -> comp.hasPermission(executor, name, DataAssetPermissions::canManageAccessRequests),
            () -> comp.isRequester(executor, name, request))
         .thenCompose(ok -> delegate.getDataAccessRequest(executor, name, request));
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String name, UID request, @Nullable Instant until, @Nullable String message) {
      return comp
         .withAuthorization(
            () -> comp.hasPermission(executor, name, DataAssetPermissions::canManageAccessRequests))
         .thenCompose(ok -> delegate.grantDataAccessRequest(executor, name, request, until, message));
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String name, UID request, String reason) {
      return comp
         .withAuthorization(
            () -> comp.hasPermission(executor, name, DataAssetPermissions::canManageAccessRequests))
         .thenCompose(ok -> delegate.rejectDataAccessRequest(executor, name, request, reason));
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String name, UID request, String reason) {
      return comp
         .withAuthorization(
            () -> comp.hasPermission(executor, name, DataAssetPermissions::canManageAccessRequests),
            () -> comp.isRequester(executor, name, request))
         .thenCompose(ok -> delegate.updateDataAccessRequest(executor, name, request, reason));
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String name, UID request, @Nullable String reason) {
      return comp
         .withAuthorization(
            () -> comp.hasPermission(executor, name, DataAssetPermissions::canManageAccessRequests),
            () -> comp.isRequester(executor, name, request))
         .thenCompose(ok -> delegate.withdrawDataAccessRequest(executor, name, request, reason));
   }

   @Override
   public CompletionStage<List<Task>> getNotifications(User executor, String name) {
      return delegate.getNotifications(executor, name);
   }

   @Override
   public CompletionStage<List<Task>> getNotifications(User executor) {
      return delegate.getNotifications(executor);
   }

   @Override
   public CompletionStage<Done> grant(User executor, String name, Authorization member, DataAssetMemberRole role) {
      return comp
         .withAuthorization(
            () -> comp.hasPermission(executor, name, DataAssetPermissions::canChangeSettings))
         .thenCompose(ok -> delegate.grant(executor, name, member, role));
   }

   @Override
   public CompletionStage<Done> revoke(User executor, String name, Authorization member) {
      return comp
         .withAuthorization(
            () -> comp.hasPermission(executor, name, DataAssetPermissions::canChangeSettings))
         .thenCompose(ok -> delegate.revoke(executor, name, member));
   }

}
