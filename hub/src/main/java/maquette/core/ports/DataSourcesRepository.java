package maquette.core.ports;

import maquette.core.entities.data.datasources.model.DataSourceProperties;
import maquette.core.ports.common.DataAssetRepository;
import maquette.core.ports.common.HasDataAccessRequests;
import maquette.core.ports.common.HasMembers;
import maquette.core.values.data.DataAssetMemberRole;

public interface DataSourcesRepository extends DataAssetRepository<DataSourceProperties>, HasDataAccessRequests, HasMembers<DataAssetMemberRole> {


}
