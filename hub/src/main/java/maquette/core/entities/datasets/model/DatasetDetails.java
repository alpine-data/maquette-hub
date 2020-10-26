package maquette.core.entities.datasets.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class DatasetDetails {

   String id;

   String name;

   String summary;

   String description;

   ActionMetadata created;

   ActionMetadata updated;

}
