package maquette.datashop.entities;

import akka.Done;
import akka.japi.function.Function3;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.ports.MembersCompanion;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.user.User;
import maquette.datashop.exceptions.AccessRequestNotFoundException;
import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.values.DataAssetProperties;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.access.DataAssetPermissions;
import maquette.datashop.values.access_requests.DataAccessRequestProperties;
import maquette.datashop.values.access_requests.DataAccessRequestState;
import maquette.datashop.values.access_requests.events.*;
import maquette.datashop.values.metadata.DataClassification;
import maquette.datashop.values.metadata.DataZone;
import maquette.datashop.values.metadata.PersonalInformation;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class AccessRequestsCompanion {

    private final UID id;

    private final DataAssetsRepository repository;

    public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, UID project,
                                                                                String reason) {
        var created = ActionMetadata.apply(executor);

        var existingRequestsCS = repository.getDataAccessRequestsCountByParent(id);
        var propertiesCS = repository.getDataAssetById(id);

        return Operators
            .compose(existingRequestsCS, propertiesCS, (existingRequests, properties) -> {
                var requestId = UID.apply(String.valueOf(existingRequests + 1));
                var request = DataAccessRequestProperties.apply(requestId, created, id, project, reason);

                if (properties.getMetadata().getClassification().equals(DataClassification.PUBLIC) &&
                    properties.getMetadata().getPersonalInformation().equals(PersonalInformation.NONE)) {
                    request = request.withEvent(Granted.apply(ActionMetadata.apply(executor), Instant.now(),
                        "Automatically " +
                            "approved access to public data asset.", "any", false));
                }

                final var requestUpdated = request;

                return repository
                    .insertOrUpdateDataAccessRequest(requestUpdated)
                    .thenApply(d -> requestUpdated);
            })
            .thenCompose(r -> r);
    }

    public CompletionStage<Done> expireDataAccessRequest(UID accessRequestId) {
        var expired = Expired.apply(Instant.now());
        return withDataAccessRequest(accessRequestId, accessRequest -> {
            accessRequest = accessRequest.withEvent(expired);
            return repository.insertOrUpdateDataAccessRequest(accessRequest);
        });
    }

    public CompletionStage<List<DataAccessRequestProperties>> getDataAccessRequests() {
        return repository.findDataAccessRequestsByAsset(id);
    }

    public CompletionStage<List<DataAccessRequestProperties>> getOpenDataAccessRequests() {
        return repository
            .findDataAccessRequestsByAsset(id)
            .thenApply(requests -> requests
                .stream()
                .filter(r -> r.getState().equals(DataAccessRequestState.REQUESTED))
                .collect(Collectors.toList()));
    }

    public CompletionStage<Optional<DataAccessRequestProperties>> findDataAccessRequestById(UID accessRequestId) {
        return repository.findDataAccessRequestById(id, accessRequestId);
    }

    public CompletionStage<DataAccessRequestProperties> getDataAccessRequestById(UID accessRequestId) {
        return findDataAccessRequestById(accessRequestId).thenApply(opt -> opt.orElseThrow(() -> AccessRequestNotFoundException
            .apply(accessRequestId)));
    }

    public CompletionStage<Done> approveDataAccessRequest(User executor, UID accessRequestId,
                                                          @Nullable String message) {
        return withDataAccessRequest(accessRequestId, request -> {
            var created = ActionMetadata.apply(executor);
            request = request.withEvent(Reviewed.apply(created, message));

            return repository.insertOrUpdateDataAccessRequest(request);
        });
    }

    public CompletionStage<Done> grantDataAccessRequest(User executor, UID accessRequestId, @Nullable Instant until,
                                                        @Nullable String message, String environment,
                                                        boolean downstreamApprovalRequired) {
        var created = ActionMetadata.apply(executor);
        var granted = Granted.apply(created, until, message, environment, downstreamApprovalRequired);

        return withDataAssetAndDataAccessRequestAndMembers(accessRequestId, (asset, accessRequest, members) -> {
            accessRequest = accessRequest.withEvent(granted);

            /*
             * Business rule definition for when a review is required.
             */
            var reviewRequired = asset.getMetadata().getClassification().equals(DataClassification.CONFIDENTIAL)
                || asset.getMetadata().getClassification().equals(DataClassification.RESTRICTED)
                || asset.getMetadata().getPersonalInformation().equals(PersonalInformation.PERSONAL_INFORMATION)
                || asset.getMetadata()
                .getPersonalInformation()
                .equals(PersonalInformation.SENSITIVE_PERSONAL_INFORMATION)
                || asset.getMetadata().getZone().equals(DataZone.GOLD);

            /*
             * Check whether executor can review by its own, if yes, we do not require the review.
             */
            var canReview = DataAssetPermissions.forUser(executor, members, false).canReview();

            /*
             * Change the state to require review from reviewer.
             */
            if (reviewRequired && !canReview) {
                accessRequest = accessRequest.withState(DataAccessRequestState.REVIEW_REQUIRED);
            }

            return repository.insertOrUpdateDataAccessRequest(accessRequest);
        });
    }

    public CompletionStage<Done> rejectDataAccessRequest(User executor, UID accessRequestId, String reason) {
        var created = ActionMetadata.apply(executor);
        var rejected = Rejected.apply(created, reason);

        return withDataAccessRequest(accessRequestId, accessRequest -> {
            accessRequest = accessRequest.withEvent(rejected);
            return repository.insertOrUpdateDataAccessRequest(accessRequest);
        });
    }

    public CompletionStage<Done> updateDataAccessRequest(User executor, UID accessRequestId, String reason) {
        var created = ActionMetadata.apply(executor);
        var requested = Requested.apply(created, reason);

        return withDataAccessRequest(accessRequestId, accessRequest -> {
            accessRequest = accessRequest.withEvent(requested);
            return repository.insertOrUpdateDataAccessRequest(accessRequest);
        });
    }

    public CompletionStage<Done> withdrawDataAccessRequest(User executor, UID accessRequestId,
                                                           @Nullable String reason) {
        var created = ActionMetadata.apply(executor);
        var withdrawn = Withdrawn.apply(created, reason);

        return withDataAccessRequest(accessRequestId, accessRequest -> {
            accessRequest = accessRequest
                .withState(DataAccessRequestState.WITHDRAWN);

            return repository.insertOrUpdateDataAccessRequest(accessRequest);
        });
    }

    private <R> CompletionStage<R> withDataAccessRequest(UID accessRequestId, Function<DataAccessRequestProperties,
        CompletionStage<R>> func) {
        return repository
            .findDataAccessRequestById(id, accessRequestId)
            .thenCompose(maybeAccessRequest -> {
                if (maybeAccessRequest.isPresent()) {
                    var accessRequest = maybeAccessRequest.get();
                    return func.apply(accessRequest);
                } else {
                    throw AccessRequestNotFoundException.apply(accessRequestId);
                }
            });
    }

    private <R> CompletionStage<R> withDataAssetAndDataAccessRequest(UID accessRequestId,
                                                                     BiFunction<DataAssetProperties,
                                                                         DataAccessRequestProperties,
                                                                         CompletionStage<R>> func) {

        return withDataAccessRequest(accessRequestId, (request) -> repository
            .getDataAssetById(id)
            .thenCompose(asset -> func.apply(asset, request)));
    }

    private <R> CompletionStage<R> withDataAssetAndDataAccessRequestAndMembers(UID accessRequestId,
                                                                               Function3<DataAssetProperties,
                                                                                   DataAccessRequestProperties,
                                                                                   List<GrantedAuthorization<DataAssetMemberRole>>, CompletionStage<R>> func) {

        return withDataAccessRequest(accessRequestId, (request) -> {
            var assetCS = repository.getDataAssetById(id);
            var membersCS = MembersCompanion.apply(id, repository).getMembers();

            return Operators
                .compose(assetCS, membersCS, (asset, members) -> func.apply(asset, request, members))
                .thenCompose(cs -> cs);
        });
    }

}
