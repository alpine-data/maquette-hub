package maquette.datashop.values;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.datashop.values.access_requests.DataAccessRequestProperties;

/**
 * A value class which contains a linked data access request and its related data asset.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class LinkedDataAsset {

    DataAccessRequestProperties request;

    DataAssetProperties asset;

}
