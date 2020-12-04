package maquette.core.entities.data.streams;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class StreamProperties implements DataAssetProperties<StreamProperties> {

   UID id;

   String title;

   String name;

   String summary;

   String description;

   DataVisibility visibility;

   DataClassification classification;

   PersonalInformation personalInformation;

   ActionMetadata created;

   ActionMetadata updated;

}
