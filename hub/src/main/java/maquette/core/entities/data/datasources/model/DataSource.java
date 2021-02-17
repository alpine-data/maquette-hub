package maquette.core.entities.data.datasources.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.data.*;
import org.apache.avro.Schema;

import java.time.Instant;
import java.util.List;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class DataSource implements DataAsset<DataSource> {

   UID id;

   String title;

   String name;

   String summary;

   DataSourceDatabaseProperties database;

   DataSourceAccessType accessType;

   DataVisibility visibility;

   DataClassification classification;

   PersonalInformation personalInformation;

   DataZone zone;

   DataAssetState state;

   int likes;

   boolean liked;

   Schema schema;

   Instant fetched;

   Long records;

   ActionMetadata created;

   ActionMetadata updated;

   List<GrantedAuthorization<DataAssetMemberRole>> members;

   List<DataAccessRequest> accessRequests;

}
