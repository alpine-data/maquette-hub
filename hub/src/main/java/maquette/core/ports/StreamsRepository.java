package maquette.core.ports;

import maquette.core.entities.data.streams.model.StreamProperties;
import maquette.core.ports.common.DataAssetRepository;
import maquette.core.ports.common.HasDataAccessRequests;
import maquette.core.ports.common.HasMembers;
import maquette.core.values.data.DataAssetMemberRole;

public interface StreamsRepository extends DataAssetRepository<StreamProperties>, HasDataAccessRequests, HasMembers<DataAssetMemberRole> {


}
