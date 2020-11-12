package maquette.core.entities.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.entities.datasets.exceptions.AccessRequestNotFoundException;
import maquette.core.ports.DatasetsRepository;
import maquette.core.values.ActionMetadata;
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

   private final String id;

   private final String projectId;

   private final String fullId;

   private final String name;

   private final DatasetsRepository repository;

   public CompletionStage<DataAccessRequest> createDataAccessRequest(User executor, String origin, String reason) {
      var created = ActionMetadata.apply(executor);

      return repository
         .getDataAccessRequestsCountByParent(getFullId())
         .thenApply(count -> DataAccessRequest.apply(String.valueOf(count + 1), created, origin, reason))
         .thenCompose(request -> repository
            .insertOrUpdateDataAccessRequest(getFullId(), request)
            .thenApply(done -> request));
   }

   public CompletionStage<Done> expireDataAccessRequest(String accessRequestId) {
      var expired = Expired.apply(Instant.now());
      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(expired);
         return repository.insertOrUpdateDataAccessRequest(getFullId(), accessRequest);
      });
   }

   public CompletionStage<List<DataAccessRequest>> getDataAccessRequests() {
      return repository.findDataAccessRequestsByParent(getFullId());
   }

   public CompletionStage<Optional<DataAccessRequest>> getDataAccessRequestById(String accessRequestId) {
      return repository.findDataAccessRequestById(getFullId(), accessRequestId);
   }

   public CompletionStage<Done> grantDataAccessRequest(User executor, String accessRequestId, @Nullable Instant until, @Nullable String message) {
      var updated = ActionMetadata.apply(executor);
      var granted = Granted.apply(updated, until, message);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(granted);
         return repository.insertOrUpdateDataAccessRequest(getFullId(), accessRequest);
      });
   }

   public CompletionStage<Done> rejectDataAccessRequest(User executor, String accessRequestId, String reason) {
      var updated = ActionMetadata.apply(executor);
      var rejected = Rejected.apply(updated, reason);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(rejected);
         return repository.insertOrUpdateDataAccessRequest(getFullId(), accessRequest);
      });
   }

   public CompletionStage<Done> updateDataAccessRequest(User executor, String accessRequestId, String reason) {
      var updated = ActionMetadata.apply(executor);
      var requested = Requested.apply(updated, reason);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(requested);
         return repository.insertOrUpdateDataAccessRequest(getFullId(), accessRequest);
      });
   }

   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String accessRequestId, @Nullable  String reason) {
      var updated = ActionMetadata.apply(executor);
      var withdrawn = Withdrawn.apply(updated, reason);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(withdrawn);
         return repository.insertOrUpdateDataAccessRequest(getFullId(), accessRequest);
      });
   }

   private <T> CompletionStage<T> withDataAccessRequest(String accessRequestId, Function<DataAccessRequest, CompletionStage<T>> func) {
      return repository
         .findDataAccessRequestById(getFullId(), accessRequestId)
         .thenCompose(maybeAccessRequest -> {
            if (maybeAccessRequest.isPresent()) {
               var accessRequest = maybeAccessRequest.get();
               return func.apply(accessRequest);
            } else {
               throw AccessRequestNotFoundException.apply(getFullId(), name, accessRequestId);
            }
         });
   }

}
