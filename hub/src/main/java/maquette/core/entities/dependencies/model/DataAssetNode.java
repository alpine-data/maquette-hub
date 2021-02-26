package maquette.core.entities.dependencies.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.UID;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataAssetNode implements DependencyNode {

   DataAssetType type;

   UID id;

}
