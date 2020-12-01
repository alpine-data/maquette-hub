package maquette.core.entities.data.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.entities.data.datasets.exceptions.AccessRequestNotFoundException;
import maquette.core.ports.HasDataAccessRequests;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.access.*;
import maquette.core.values.user.User;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class AccessRequests {

   private final UID id;

   private final HasDataAccessRequests repository;

   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, UID project, String reason) {
      var created = ActionMetadata.apply(executor);

      return repository
         .getDataAccessRequestsCountByParent(id)
         .thenApply(count -> {
            var requestId = UID.apply(String.valueOf(count + 1));
            return DataAccessRequestProperties.apply(requestId, created, id, project, reason);
         })
         .thenCompose(request -> repository
            .insertOrUpdateDataAccessRequest(request)
            .thenApply(done -> request));
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

   public CompletionStage<Optional<DataAccessRequestProperties>> findDataAccessRequestById(UID accessRequestId) {
      return repository.findDataAccessRequestById(id, accessRequestId);
   }

   public CompletionStage<DataAccessRequestProperties> getDataAccessRequestById(UID accessRequestId) {
      return findDataAccessRequestById(accessRequestId).thenApply(opt -> opt.orElseThrow(() -> AccessRequestNotFoundException.apply(accessRequestId)));
   }

   public CompletionStage<Done> grantDataAccessRequest(User executor, UID accessRequestId, @Nullable Instant until, @Nullable String message) {
      var updated = ActionMetadata.apply(executor);
      var granted = Granted.apply(updated, until, message);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(granted);
         return repository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   public CompletionStage<Done> rejectDataAccessRequest(User executor, UID accessRequestId, String reason) {
      var updated = ActionMetadata.apply(executor);
      var rejected = Rejected.apply(updated, reason);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(rejected);
         return repository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   public CompletionStage<Done> updateDataAccessRequest(User executor, UID accessRequestId, String reason) {
      var updated = ActionMetadata.apply(executor);
      var requested = Requested.apply(updated, reason);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(requested);
         return repository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   public CompletionStage<Done> withdrawDataAccessRequest(User executor, UID accessRequestId, @Nullable  String reason) {
      var updated = ActionMetadata.apply(executor);
      var withdrawn = Withdrawn.apply(updated, reason);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(withdrawn);
         return repository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   private <T> CompletionStage<T> withDataAccessRequest(UID accessRequestId, Function<DataAccessRequestProperties, CompletionStage<T>> func) {
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
