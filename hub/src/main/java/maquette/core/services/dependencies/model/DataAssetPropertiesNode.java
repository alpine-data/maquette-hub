package maquette.core.services.dependencies.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.values.UID;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataAssetPropertiesNode implements DependencyPropertiesNode {

   String type;

   UID id;

   DataAssetProperties properties;

}
