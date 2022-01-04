package maquette.datashop.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.user.User;
import maquette.datashop.ports.Workspace;
import maquette.datashop.values.DataAsset;
import maquette.datashop.values.DataAssetProperties;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.access.DataAssetPermissions;
import maquette.datashop.values.access_requests.DataAccessRequest;
import maquette.datashop.values.access_requests.DataAccessRequestProperties;
import maquette.datashop.values.metadata.DataAssetMetadata;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DataAssetServicesSecured implements DataAssetServices {

    private final DataAssetServicesCompanion comp;

    private final DataAssetServices delegate;

    @Override
    public CompletionStage<DataAssetProperties> create(User executor, String type, DataAssetMetadata metadata,
                                                       Authorization owner, Authorization steward,
                                                       @Nullable Object customSettings) {
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
                () -> comp.isVisible(name),
                () -> isMemberCS,
                () -> isSubscribedCS)
            .thenCompose(ok -> delegate.get(executor, name))
            .thenCompose(entity -> {
                var isOwnerCS = comp.isMember(executor, name, DataAssetMemberRole.OWNER);
                var isStewardCS = comp.isMember(executor, name, DataAssetMemberRole.STEWARD);

                return Operators
                    .compose(isMemberCS, isSubscribedCS, isOwnerCS, isStewardCS, (isMember, isSubscribed, isOwner,
                                                                                  isSteward) -> {
                        var permissions = DataAssetPermissions.apply(isOwner, isSteward, false, false, isMember,
                            isSubscribed);

                        // adopt permissions for access requests and filter not accessible access requests.
                        if (permissions.canManageAccessRequests() || permissions.canReview()) {
                            var requests = entity
                                .getAccessRequests()
                                .stream()
                                .map(request -> request.withCanGrant(permissions.canManageAccessRequests()))
                                .map(request -> request.withCanReview(permissions.canReview()))
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
                    () -> comp.isMember(executor, entity.getMetadata().getName()))))
            .thenCompose(Operators::allOf)
            .thenApply(datasets -> datasets
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()));
    }

    @Override
    public CompletionStage<List<DataAssetProperties>> query(User executor, String query) {
        return delegate
            .query(executor, query)
            .thenApply(datasets -> datasets
                .stream()
                .map(entity -> comp.filterAuthorized(
                    entity,
                    () -> comp.isVisible(entity.getMetadata().getName()),
                    () -> comp.isSuperUser(executor),
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
    public CompletionStage<Done> decline(User executor, String name, String reason) {
        return comp
            .withAuthorization(
                () -> comp.hasPermission(executor, name, DataAssetPermissions::canManageState))
            .thenCompose(ok -> delegate.decline(executor, name, reason));
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
                () -> comp.isSuperUser(executor),
                () -> comp.hasPermission(executor, name, DataAssetPermissions::canChangeSettings))
            .thenCompose(ok -> delegate.remove(executor, name));
    }

    @Override
    public CompletionStage<Done> requestReview(User executor, String name, String message) {
        return comp
            .withAuthorization(
                () -> comp.hasPermission(executor, name, DataAssetPermissions::canChangeSettings))
            .thenCompose(ok -> delegate.requestReview(executor, name, message));
    }

    @Override
    public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String name,
                                                                                String workspace, String reason) {
        return comp
            .withAuthorization(
                () -> comp.isVisible(name),
                () -> comp.hasPermission(executor, name, DataAssetPermissions::canConsume))
            .thenCompose(ok -> delegate.createDataAccessRequest(executor, name, workspace, reason));
    }

    @Override
    public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String name, UID request) {
        var canManageAccessRequestsCS = comp.hasPermission(executor, name,
            DataAssetPermissions::canManageAccessRequests);
        var canReviewAccessRequestsCS = comp.hasPermission(executor, name,
            DataAssetPermissions::canReview);
        var isRequesterCS = comp.isRequester(executor, name, request);
        var dataAccessRequestCS = comp
            .withAuthorization(
                () -> comp.isSuperUser(executor),
                () -> canManageAccessRequestsCS,
                () -> isRequesterCS)
            .thenCompose(ok -> delegate.getDataAccessRequest(executor, name, request));

        return Operators.compose(
            canManageAccessRequestsCS, canReviewAccessRequestsCS, isRequesterCS, dataAccessRequestCS,
            (canManageAccessRequests, canReviewAccessRequest, isRequester, dataAccessRequest) -> dataAccessRequest
                .withCanRequest(isRequester)
                .withCanReview(canReviewAccessRequest)
                .withCanGrant(canManageAccessRequests));
    }

    @Override
    public CompletionStage<List<DataAccessRequestProperties>> getDataAccessRequests(User executor, String name) {
        return comp
            .withAuthorization(
                () -> comp.isSuperUser(executor),
                () -> comp.hasPermission(executor, name, DataAssetPermissions::canManageAccessRequests))
            .thenCompose(ok -> delegate.getDataAccessRequests(executor, name));
    }

    @Override
    public CompletionStage<Done> approveDataAccessRequest(User executor, String name, UID request,
                                                          @Nullable String message) {
        return comp
            .withAuthorization(
                () -> comp.hasPermission(executor, name, DataAssetPermissions::canReview))
            .thenCompose(ok -> delegate.approveDataAccessRequest(executor, name, request, message));
    }

    @Override
    public CompletionStage<Done> grantDataAccessRequest(User executor, String name, UID request,
                                                        @Nullable Instant until, @Nullable String message,
                                                        String environment, boolean downstreamApprovalRequired) {
        return comp
            .withAuthorization(
                () -> comp.hasPermission(executor, name, DataAssetPermissions::canManageAccessRequests))
            .thenCompose(ok -> delegate.grantDataAccessRequest(executor, name, request, until, message, environment,
                downstreamApprovalRequired));
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
    public CompletionStage<Done> withdrawDataAccessRequest(User executor, String name, UID request,
                                                           @Nullable String reason) {
        return comp
            .withAuthorization(
                () -> comp.hasPermission(executor, name, DataAssetPermissions::canManageAccessRequests),
                () -> comp.isRequester(executor, name, request))
            .thenCompose(ok -> delegate.withdrawDataAccessRequest(executor, name, request, reason));
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

    @Override
    public CompletionStage<List<Workspace>> getUsersWorkspaces(User executor) {
        return delegate.getUsersWorkspaces(executor);
    }

}
