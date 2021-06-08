package maquette.core.entities.data.model.access;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataAccessTokenNarrowed {

   ActionMetadata created;

   String name;

   String description;

   String key;

}
