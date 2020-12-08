package maquette.core.ports;

import maquette.core.entities.data.collections.model.CollectionProperties;
import maquette.core.ports.common.DataAssetRepository;
import maquette.core.ports.common.HasDataAccessRequests;
import maquette.core.ports.common.HasMembers;
import maquette.core.values.data.DataAssetMemberRole;

public interface CollectionsRepository extends DataAssetRepository<CollectionProperties>, HasDataAccessRequests, HasMembers<DataAssetMemberRole> {

}
