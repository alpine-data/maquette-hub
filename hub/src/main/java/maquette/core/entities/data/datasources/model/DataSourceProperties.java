package maquette.core.entities.data.datasources.model;

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

import java.time.Instant;

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

   Schema schema;

   Instant fetched;

   Long records;

   ActionMetadata created;

   ActionMetadata updated;



}
