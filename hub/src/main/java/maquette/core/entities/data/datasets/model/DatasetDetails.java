package maquette.core.entities.data.datasets.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessToken;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;

import java.util.List;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class DatasetDetails {

   String id;

   String title;

   String name;

   String summary;

   String description;

   DataVisibility visibility;

   DataClassification classification;

   PersonalInformation personalInformation;

   ActionMetadata created;

   ActionMetadata updated;

   List<UserAuthorization> owners;

   List<DataAccessRequest> accessRequests;

   List<DataAccessToken> accessTokens;

}
