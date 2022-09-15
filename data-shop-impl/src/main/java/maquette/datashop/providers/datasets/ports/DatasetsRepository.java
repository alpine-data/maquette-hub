package maquette.datashop.providers.datasets.ports;

import akka.Done;
import maquette.core.values.UID;
import maquette.datashop.providers.datasets.model.CommittedRevision;
import maquette.datashop.providers.datasets.model.DatasetVersion;
import maquette.datashop.providers.datasets.model.Revision;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DatasetsRepository {

    CompletionStage<List<Revision>> findAllRevisions(UID dataset);

    CompletionStage<List<CommittedRevision>> findAllVersions(UID dataset);

    CompletionStage<Optional<Revision>> findRevisionById(UID dataset, UID revision);

    CompletionStage<Optional<CommittedRevision>> findRevisionByVersion(UID dataset, DatasetVersion version);

    CompletionStage<Done> insertOrUpdateRevision(UID dataset, Revision revision);

    RecordsStore getRecordsStore(UID dataset);

}
