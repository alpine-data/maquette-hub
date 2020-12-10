package maquette.core.entities.data.collections.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.data.*;

import java.util.List;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Collection implements DataAsset<Collection> {

   UID id;

   String title;

   String name;

   String summary;

   DataVisibility visibility;

   DataClassification classification;

   PersonalInformation personalInformation;

   ActionMetadata created;

   ActionMetadata updated;

   List<GrantedAuthorization<DataAssetMemberRole>> members;

   List<DataAccessRequest> accessRequests;

}
