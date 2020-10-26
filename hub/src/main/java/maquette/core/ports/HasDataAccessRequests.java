package maquette.core.ports;

import akka.Done;
import maquette.core.values.access.DataAccessRequest;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface HasDataAccessRequests {

   CompletionStage<Optional<DataAccessRequest>> findDataAccessRequestById(String parentId, String id);

   CompletionStage<Done> insertOrUpdateDataAccessRequest(String parentId, DataAccessRequest request);

   CompletionStage<List<DataAccessRequest>> findDataAccessRequestsByParent(String parentId);

   CompletionStage<Done> removeDataAccessRequest(String parentId, String id);

}
