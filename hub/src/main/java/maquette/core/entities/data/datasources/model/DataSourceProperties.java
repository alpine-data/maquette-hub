package maquette.core.entities.data.datasources.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.*;
import org.apache.avro.Schema;

import java.time.Instant;
import java.util.Objects;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class DataSourceProperties implements DataAssetProperties<DataSourceProperties> {

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

   Schema schema;

   Instant fetched;

   Long records;

   ActionMetadata created;

   ActionMetadata updated;

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
