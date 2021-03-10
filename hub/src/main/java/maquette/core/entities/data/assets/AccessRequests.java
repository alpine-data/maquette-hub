package maquette.core.entities.data.assets;

import akka.Done;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.exceptions.AccessRequestNotFoundException;
import maquette.core.ports.common.HasDataAccessRequests;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.access.*;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccessRequests<T extends DataAssetProperties<T>> {

   private final UID id;

   private final HasDataAccessRequests requestsRepository;

   private final Supplier<CompletionStage<T>> getProperties;

   public static <T extends DataAssetProperties<T>> AccessRequests<T> apply(UID id, HasDataAccessRequests requestsRepository, Supplier<CompletionStage<T>> getProperties) {
      return new AccessRequests<T>(id, requestsRepository, getProperties);
   }

   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, UID project, String reason) {
      var created = ActionMetadata.apply(executor);

      var existingRequestsCS = requestsRepository.getDataAccessRequestsCountByParent(id);
      var propertiesCS = getProperties.get();

      return Operators
         .compose(existingRequestsCS, propertiesCS, (existingRequests, properties) -> {
            var requestId = UID.apply(String.valueOf(existingRequests + 1));
            var request = DataAccessRequestProperties.apply(requestId, created, id, project, reason);

            if (properties.getClassification().equals(DataClassification.PUBLIC) &&
               properties.getPersonalInformation().equals(PersonalInformation.NONE)) {
               request.addEvent(Granted.apply(ActionMetadata.apply(executor), Instant.now(), "Automatically approved access to public data asset."));
            }

            return requestsRepository
               .insertOrUpdateDataAccessRequest(request)
               .thenApply(d -> request);
         })
         .thenCompose(r -> r);
   }

   public CompletionStage<Done> expireDataAccessRequest(UID accessRequestId) {
      var expired = Expired.apply(Instant.now());
      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(expired);
         return requestsRepository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   public CompletionStage<List<DataAccessRequestProperties>> getDataAccessRequests() {
      return requestsRepository.findDataAccessRequestsByAsset(id);
   }

   public CompletionStage<List<DataAccessRequestProperties>> getOpenDataAccessRequests() {
      return requestsRepository
         .findDataAccessRequestsByAsset(id)
         .thenApply(requests -> requests
            .stream()
            .filter(r -> r.getStatus().equals(DataAccessRequestStatus.REQUESTED))
            .collect(Collectors.toList()));
   }

   public CompletionStage<Optional<DataAccessRequestProperties>> findDataAccessRequestById(UID accessRequestId) {
      return requestsRepository.findDataAccessRequestById(id, accessRequestId);
   }

   public CompletionStage<DataAccessRequestProperties> getDataAccessRequestById(UID accessRequestId) {
      return findDataAccessRequestById(accessRequestId).thenApply(opt -> opt.orElseThrow(() -> AccessRequestNotFoundException.apply(accessRequestId)));
   }

   public CompletionStage<Done> grantDataAccessRequest(User executor, UID accessRequestId, @Nullable Instant until, @Nullable String message) {
      var created = ActionMetadata.apply(executor);
      var granted = Granted.apply(created, until, message);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(granted);
         return requestsRepository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   public CompletionStage<Done> rejectDataAccessRequest(User executor, UID accessRequestId, String reason) {
      var created = ActionMetadata.apply(executor);
      var rejected = Rejected.apply(created, reason);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(rejected);
         return requestsRepository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   public CompletionStage<Done> updateDataAccessRequest(User executor, UID accessRequestId, String reason) {
      var created = ActionMetadata.apply(executor);
      var requested = Requested.apply(created, reason);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(requested);
         return requestsRepository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   public CompletionStage<Done> withdrawDataAccessRequest(User executor, UID accessRequestId, @Nullable String reason) {
      var created = ActionMetadata.apply(executor);
      var withdrawn = Withdrawn.apply(created, reason);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(withdrawn);
         return requestsRepository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   private <R> CompletionStage<R> withDataAccessRequest(UID accessRequestId, Function<DataAccessRequestProperties, CompletionStage<R>> func) {
      return requestsRepository
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

}
