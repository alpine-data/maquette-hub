package maquette.core.entities.data;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.exceptions.AccessRequestNotFoundException;
import maquette.core.entities.data.ports.DataAssetsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.access.*;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class AccessRequests {

   private final UID id;

   private final DataAssetsRepository repository;

   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, UID project, String reason) {
      var created = ActionMetadata.apply(executor);

      var existingRequestsCS = repository.getDataAccessRequestsCountByParent(id);
      var propertiesCS = repository.getEntityById(id);

      return Operators
         .compose(existingRequestsCS, propertiesCS, (existingRequests, properties) -> {
            var requestId = UID.apply(String.valueOf(existingRequests + 1));
            var request = DataAccessRequestProperties.apply(requestId, created, id, project, reason);

            if (properties.getMetadata().getClassification().equals(DataClassification.PUBLIC) &&
               properties.getMetadata().getPersonalInformation().equals(PersonalInformation.NONE)) {
               request.addEvent(Granted.apply(ActionMetadata.apply(executor), Instant.now(), "Automatically approved access to public data asset."));
            }

            return repository
               .insertOrUpdateDataAccessRequest(request)
               .thenApply(d -> request);
         })
         .thenCompose(r -> r);
   }

   public CompletionStage<Done> expireDataAccessRequest(UID accessRequestId) {
      var expired = Expired.apply(Instant.now());
      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(expired);
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
            .filter(r -> r.getStatus().equals(DataAccessRequestStatus.REQUESTED))
            .collect(Collectors.toList()));
   }

   public CompletionStage<Optional<DataAccessRequestProperties>> findDataAccessRequestById(UID accessRequestId) {
      return repository.findDataAccessRequestById(id, accessRequestId);
   }

   public CompletionStage<DataAccessRequestProperties> getDataAccessRequestById(UID accessRequestId) {
      return findDataAccessRequestById(accessRequestId).thenApply(opt -> opt.orElseThrow(() -> AccessRequestNotFoundException.apply(accessRequestId)));
   }

   public CompletionStage<Done> grantDataAccessRequest(User executor, UID accessRequestId, @Nullable Instant until, @Nullable String message) {
      var created = ActionMetadata.apply(executor);
      var granted = Granted.apply(created, until, message);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(granted);
         return repository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   public CompletionStage<Done> rejectDataAccessRequest(User executor, UID accessRequestId, String reason) {
      var created = ActionMetadata.apply(executor);
      var rejected = Rejected.apply(created, reason);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(rejected);
         return repository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   public CompletionStage<Done> updateDataAccessRequest(User executor, UID accessRequestId, String reason) {
      var created = ActionMetadata.apply(executor);
      var requested = Requested.apply(created, reason);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(requested);
         return repository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   public CompletionStage<Done> withdrawDataAccessRequest(User executor, UID accessRequestId, @Nullable String reason) {
      var created = ActionMetadata.apply(executor);
      var withdrawn = Withdrawn.apply(created, reason);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(withdrawn);
         return repository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   private <R> CompletionStage<R> withDataAccessRequest(UID accessRequestId, Function<DataAccessRequestProperties, CompletionStage<R>> func) {
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

}
