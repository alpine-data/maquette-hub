package maquette.core.entities.data.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.entities.data.datasets.exceptions.AccessRequestNotFoundException;
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
         .getDataAccessRequestsCountByParent(projectId, id)
         .thenApply(count -> DataAccessRequest.apply(String.valueOf(count + 1), created, projectId, id, origin, reason))
         .thenCompose(request -> repository
            .insertOrUpdateDataAccessRequest(request)
            .thenApply(done -> request));
   }

   public CompletionStage<Done> expireDataAccessRequest(String accessRequestId) {
      var expired = Expired.apply(Instant.now());
      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(expired);
         return repository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   public CompletionStage<List<DataAccessRequest>> getDataAccessRequests() {
      return repository.findDataAccessRequestsByParent(projectId, id);
   }

   public CompletionStage<Optional<DataAccessRequest>> getDataAccessRequestById(String accessRequestId) {
      return repository.findDataAccessRequestById(projectId, id, accessRequestId);
   }

   public CompletionStage<Done> grantDataAccessRequest(User executor, String accessRequestId, @Nullable Instant until, @Nullable String message) {
      var updated = ActionMetadata.apply(executor);
      var granted = Granted.apply(updated, until, message);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(granted);
         return repository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   public CompletionStage<Done> rejectDataAccessRequest(User executor, String accessRequestId, String reason) {
      var updated = ActionMetadata.apply(executor);
      var rejected = Rejected.apply(updated, reason);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(rejected);
         return repository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   public CompletionStage<Done> updateDataAccessRequest(User executor, String accessRequestId, String reason) {
      var updated = ActionMetadata.apply(executor);
      var requested = Requested.apply(updated, reason);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(requested);
         return repository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String accessRequestId, @Nullable  String reason) {
      var updated = ActionMetadata.apply(executor);
      var withdrawn = Withdrawn.apply(updated, reason);

      return withDataAccessRequest(accessRequestId, accessRequest -> {
         accessRequest.addEvent(withdrawn);
         return repository.insertOrUpdateDataAccessRequest(accessRequest);
      });
   }

   private <T> CompletionStage<T> withDataAccessRequest(String accessRequestId, Function<DataAccessRequest, CompletionStage<T>> func) {
      return repository
         .findDataAccessRequestById(projectId, id, accessRequestId)
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
