package maquette.core.entities.data.streams.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import org.apache.avro.Schema;

import java.util.Optional;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class StreamProperties implements DataAssetProperties<StreamProperties> {

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

   public Optional<Schema> getSchema() {
      return Optional.ofNullable(schema);
   }

}