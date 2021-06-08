package maquette.core.entities.data.model.tasks;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.values.data.DataAssetPermissions;


@Value
@AllArgsConstructor(staticName = "apply")
public class ReviewAsset implements Task {

   DataAssetProperties asset;

   @Override
   public boolean canExecuteTask(DataAssetPermissions permissions) {
      return permissions.canReview();
   }

}
