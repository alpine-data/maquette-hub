package maquette.core.entities.data.datasets.model.tasks;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetPermissions;

@Value
@AllArgsConstructor(staticName = "apply")
public class ReviewAccessRequest implements Task {

   String name;

   String project;

   UID id;

   @Override
   public boolean canExecuteTask(DataAssetPermissions permissions) {
      return permissions.canManageAccessRequests();
   }
}
