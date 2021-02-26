package maquette.core.services.dependencies.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.dependencies.model.DataAssetType;
import maquette.core.entities.dependencies.model.DependencyNode;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetProperties;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataAssetPropertiesNode implements DependencyPropertiesNode {

   DataAssetType type;

   UID id;

   DataAssetProperties<?> properties;

}
