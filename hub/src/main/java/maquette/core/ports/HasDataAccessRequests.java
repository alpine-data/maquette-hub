package maquette.core.ports;

import akka.Done;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequestProperties;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface HasDataAccessRequests {

   CompletionStage<Optional<DataAccessRequestProperties>> findDataAccessRequestById(UID asset, UID request);

   CompletionStage<Done> insertOrUpdateDataAccessRequest(DataAccessRequestProperties request);

   CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByProject(UID project);

   CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByAsset(UID asset);

   default CompletionStage<Integer> getDataAccessRequestsCountByParent(UID asset) {
      return findDataAccessRequestsByAsset(asset).thenApply(List::size);
   }

   CompletionStage<Done> removeDataAccessRequest(UID asset, UID id);

}
