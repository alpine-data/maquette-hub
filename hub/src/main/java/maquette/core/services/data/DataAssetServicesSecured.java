package maquette.core.services.data;

import akka.Done;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.data.DataAssetEntity;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.data.DataAsset;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.data.DataAssetPermissions;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.data.logs.DataAccessLogEntry;
import maquette.core.values.user.User;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataAssetServicesSecured<P extends DataAssetProperties<P>, E extends DataAssetEntity<P>, EN extends DataAssetEntities<P, E>> implements DataAssetServices<P, E> {

   DataAssetServices<P, E> delegate;

   DataAssetCompanion<P, EN> companion;

   public static <P extends DataAssetProperties<P>, E extends DataAssetEntity<P>, EN extends DataAssetEntities<P, E>> DataAssetServicesSecured<P, E, EN> apply(
      DataAssetServices<P, E> delegate, DataAssetCompanion<P, EN> companion) {

      return new DataAssetServicesSecured<>(delegate, companion);
   }

   @Override
   public CompletionStage<Done> grant(User executor, String asset, Authorization member, DataAssetMemberRole role) {
      return companion
         .withAuthorization(
            () -> companion.hasPermission(executor, asset, DataAssetPermissions::canChangeSettings))
         .thenCompose(ok -> delegate.grant(executor, asset, member, role));
   }

   @Override
   public CompletionStage<Done> revoke(User executor, String asset, Authorization member) {
      return companion
         .withAuthorization(
            () -> companion.hasPermission(executor, asset, DataAssetPermissions::canChangeSettings))
         .thenCompose(ok -> delegate.revoke(executor, asset, member));
   }

   @Override
   public CompletionStage<List<DataAccessLogEntry>> getAccessLogs(User executor, String asset) {
      return companion
         .withAuthorization(
            () -> companion.hasPermission(executor, asset, DataAssetPermissions::canReviewLogs))
         .thenCompose(ok -> delegate.getAccessLogs(executor, asset));
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String asset, String project, String reason) {
      return companion
         .withAuthorization(
            () -> companion.isVisible(asset),
            () -> companion.hasPermission(executor, asset, DataAssetPermissions::canConsume))
         .thenCompose(ok -> delegate.createDataAccessRequest(executor, asset, project, reason));
   }

   @Override
   public <T extends DataAsset<T>> CompletionStage<T> get(User executor, String asset, Function<E, CompletionStage<T>> mapEntityToAsset) {
      var isMemberCS = companion.isMember(executor, asset);
      var isSubscribedCS = companion.isSubscribedConsumer(executor, asset);

      return companion
         .withAuthorization(
            executor::isSystemUserCS,
            () -> companion.isVisible(asset),
            () -> isMemberCS,
            () -> isSubscribedCS)
         .thenCompose(ok -> delegate.get(executor, asset, mapEntityToAsset))
         .thenCompose(as -> {
            var isOwnerCS = companion.isMember(executor, asset, DataAssetMemberRole.OWNER);

            return Operators
               .compose(isMemberCS, isSubscribedCS, isOwnerCS, (isMember, isSubscribed, isOwner) -> {
                  if (isOwner) {
                     var requests = as
                        .getAccessRequests()
                        .stream()
                        .map(request -> request.withCanGrant(true))
                        .collect(Collectors.toList());

                     return CompletableFuture.completedFuture(as.withAccessRequests(requests));
                  } else {
                     return Operators
                        .allOf(as
                           .getAccessRequests()
                           .stream()
                           .map(request -> companion.filterRequester(executor, as.getName(), request.getId(), request)))
                        .thenApply(all -> all
                           .stream()
                           .filter(Optional::isPresent)
                           .map(Optional::get)
                           .map(request -> request.withCanRequest(true))
                           .collect(Collectors.toList()))
                        .thenApply(as::withAccessRequests);
                  }
               })
               .thenCompose(cs -> cs);
         });
   }

   @Override
   public CompletionStage<List<P>> list(User executor) {
      return delegate
         .list(executor)
         .thenApply(datasets -> datasets
            .stream()
            .map(entity -> companion.filterAuthorized(
               entity,
               executor::isSystemUserCS,
               () -> companion.isVisible(entity.getName()),
               () -> companion.isMember(executor, entity.getName()),
               () -> companion.isSubscribedConsumer(executor, entity.getName()))))
         .thenCompose(Operators::allOf)
         .thenApply(datasets -> datasets
            .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList()));
   }

   @Override
   public CompletionStage<Done> remove(User executor, String asset) {
      return companion
         .withAuthorization(
            executor::isSystemUserCS,
            () -> companion.hasPermission(executor, asset, DataAssetPermissions::canChangeSettings))
         .thenCompose(ok -> delegate.remove(executor, asset));
   }

   @Override
   public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String asset, UID request) {
      return companion
         .withAuthorization(
            executor::isSystemUserCS,
            () -> companion.hasPermission(executor, asset, DataAssetPermissions::canChangeSettings))
         .thenCompose(ok -> delegate.getDataAccessRequest(executor, asset, request));
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String asset, UID request, @Nullable Instant until, @Nullable String message) {
      return companion
         .withAuthorization(
            () -> companion.hasPermission(executor, asset, DataAssetPermissions::canChangeSettings))
         .thenCompose(ok -> delegate.grantDataAccessRequest(executor, asset, request, until, message));
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String asset, UID request, String reason) {
      return companion
         .withAuthorization(
            () -> companion.hasPermission(executor, asset, DataAssetPermissions::canChangeSettings))
         .thenCompose(ok -> delegate.rejectDataAccessRequest(executor, asset, request, reason));
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String asset, UID request, String reason) {
      return companion
         .withAuthorization(
            () -> companion.hasPermission(executor, asset, DataAssetPermissions::canChangeSettings),
            () -> companion.isRequester(executor, asset, request))
         .thenCompose(ok -> delegate.updateDataAccessRequest(executor, asset, request, reason));
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String asset, UID request, @Nullable String reason) {
      return companion
         .withAuthorization(
            () -> companion.hasPermission(executor, asset, DataAssetPermissions::canChangeSettings),
            () -> companion.isRequester(executor, asset, request))
         .thenCompose(ok -> delegate.withdrawDataAccessRequest(executor, asset, request, reason));
   }

}
