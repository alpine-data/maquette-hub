package maquette.core.entities.data.streams.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.data.*;
import org.apache.avro.Schema;

import java.util.List;
import java.util.Optional;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Stream implements DataAsset<Stream> {

   UID id;

   String title;

   String name;

   String summary;

   Retention retention;

   Schema schema;

   DataVisibility visibility;

   DataClassification classification;

   PersonalInformation personalInformation;

   ActionMetadata created;

   ActionMetadata updated;

   List<GrantedAuthorization<DataAssetMemberRole>> members;

   List<DataAccessRequest> accessRequests;

   public Optional<Schema> getSchema() {
      return Optional.ofNullable(schema);
   }

}
