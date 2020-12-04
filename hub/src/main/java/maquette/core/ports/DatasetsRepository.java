package maquette.core.ports;

import akka.Done;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasets.model.DatasetVersion;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.data.datasets.model.revisions.Revision;
import maquette.core.ports.common.DataAssetRepository;
import maquette.core.ports.common.HasDataAccessRequests;
import maquette.core.ports.common.HasMembers;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetMemberRole;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DatasetsRepository extends DataAssetRepository<DatasetProperties>, HasDataAccessRequests, HasMembers<DataAssetMemberRole> {

   CompletionStage<List<Revision>> findAllRevisions(UID dataset);

   CompletionStage<List<CommittedRevision>> findAllVersions(UID dataset);

   CompletionStage<Optional<Revision>> findRevisionById(UID dataset, UID revision);

   CompletionStage<Optional<CommittedRevision>> findRevisionByVersion(UID dataset, DatasetVersion version);

   CompletionStage<Done> insertOrUpdateRevision(UID dataset, Revision revision);

}
