package maquette.core.entities.data.model.tasks;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.values.data.DataAssetPermissions;

@Value
@AllArgsConstructor(staticName = "apply")
public class AnswerAccessRequests implements Task {

   DataAssetProperties asset;

   /**
    * Number of open access requests.
    */
   int openRequests;

   @Override
   public boolean canExecuteTask(DataAssetPermissions permissions) {
      return permissions.canManageAccessRequests();
   }

}
