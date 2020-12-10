package maquette.core.entities.data.datasets.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessToken;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.data.*;

import java.util.List;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Dataset implements DataAsset<Dataset> {

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

   List<DataAccessToken> accessTokens;

   List<CommittedRevision> versions;

   @JsonIgnore
   public DatasetProperties getProperties() {
      return DatasetProperties.apply(
         id, title, name, summary, visibility, classification, personalInformation, created, updated);
   }

}
