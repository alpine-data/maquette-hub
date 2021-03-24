package maquette.asset_providers.datasets;

import akka.Done;
import maquette.asset_providers.datasets.model.CommittedRevision;
import maquette.asset_providers.datasets.model.DatasetVersion;
import maquette.asset_providers.datasets.model.Revision;
import maquette.core.values.UID;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DatasetsRepository {

   CompletionStage<List<Revision>> findAllRevisions(UID dataset);

   CompletionStage<List<CommittedRevision>> findAllVersions(UID dataset);

   CompletionStage<Optional<Revision>> findRevisionById(UID dataset, UID revision);

   CompletionStage<Optional<CommittedRevision>> findRevisionByVersion(UID dataset, DatasetVersion version);

   CompletionStage<Done> insertOrUpdateRevision(UID dataset, Revision revision);

}
