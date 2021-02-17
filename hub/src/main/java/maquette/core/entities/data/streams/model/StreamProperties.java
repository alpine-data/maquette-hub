package maquette.core.entities.data.streams.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.*;
import org.apache.avro.Schema;

import java.util.Objects;
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

   DataZone zone;

   DataAssetState state;

   ActionMetadata created;

   ActionMetadata updated;

   public Optional<Schema> getSchema() {
      return Optional.ofNullable(schema);
   }

   public DataZone getZone() {
      if (Objects.isNull(zone)) {
         return DataZone.RAW;
      } else {
         return zone;
      }
   }

   public DataAssetState getState() {
      if (Objects.isNull(state)) {
         return DataAssetState.APPROVED;
      } else {
         return state;
      }
   }

}
