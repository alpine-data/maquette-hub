package maquette.core.ports;

import akka.Done;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasets.model.DatasetVersion;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.data.datasets.model.revisions.Revision;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetMemberRole;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DatasetsRepository extends HasDataAccessRequests, HasMembers<DataAssetMemberRole> {

   CompletionStage<List<DatasetProperties>> findAllDatasets();

   CompletionStage<List<Revision>> findAllRevisions(UID dataset);

   CompletionStage<List<CommittedRevision>> findAllVersions(UID dataset);

   CompletionStage<Optional<DatasetProperties>> findDatasetById(UID dataset);

   CompletionStage<Optional<DatasetProperties>> findDatasetByName(String name);

   CompletionStage<Optional<Revision>> findRevisionById(UID dataset, UID revision);

   CompletionStage<Optional<CommittedRevision>> findRevisionByVersion(UID dataset, DatasetVersion version);

   CompletionStage<Done> insertOrUpdateDataset(DatasetProperties dataset);

   CompletionStage<Done> insertOrUpdateRevision(UID dataset, Revision revision);

}
