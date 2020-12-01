package maquette.core.entities.data.repositories;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetProperties;

@Value
@AllArgsConstructor(staticName = "apply")
public class RepositoryProperties implements DataAssetProperties {

   UID id;

   String title;

   String name;

   String summary;

   ActionMetadata created;

   ActionMetadata updated;

}
