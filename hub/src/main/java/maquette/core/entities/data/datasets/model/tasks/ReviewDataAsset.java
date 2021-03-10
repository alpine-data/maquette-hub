package maquette.core.entities.data.datasets.model.tasks;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.data.DataAssetPermissions;
import maquette.core.values.data.DataAssetProperties;

@Value
@AllArgsConstructor(staticName = "apply")
public class ReviewDataAsset implements Task {

   DataAssetProperties<?> asset;

   @Override
   public boolean canExecuteTask(DataAssetPermissions permissions) {
      return permissions.canManageState();
   }
}
