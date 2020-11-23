package maquette.core.ports;

import akka.Done;
import maquette.core.values.access.DataAccessRequest;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface HasDataAccessRequests {

   CompletionStage<Optional<DataAccessRequest>> findDataAccessRequestById(String targetProjectId, String targetId, String id);

   CompletionStage<Done> insertOrUpdateDataAccessRequest(DataAccessRequest request);

   CompletionStage<List<DataAccessRequest>> findDataAccessRequestsByParent(String targetProjectId, String targetId);

   CompletionStage<List<DataAccessRequest>> findDataAccessRequestsByOrigin(String originId);

   default CompletionStage<Integer> getDataAccessRequestsCountByParent(String targetProjectId, String targetId) {
      return findDataAccessRequestsByParent(targetProjectId, targetId).thenApply(List::size);
   }

   CompletionStage<Done> removeDataAccessRequest(String targetProjectId, String targetId, String id);

}
