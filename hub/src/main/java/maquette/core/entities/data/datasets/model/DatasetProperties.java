package maquette.core.entities.data.datasets.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.*;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class DatasetProperties implements DataAssetProperties<DatasetProperties> {

   UID id;

   String title;

   String name;

   String summary;

   DataVisibility visibility;

   DataClassification classification;

   PersonalInformation personalInformation;

   DataZone zone;

   DataAssetState state;

   ActionMetadata created;

   ActionMetadata updated;

}
