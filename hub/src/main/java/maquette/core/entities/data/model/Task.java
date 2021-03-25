package maquette.core.entities.data.model;

import maquette.core.values.data.DataAssetPermissions;

public interface Task {

   boolean canExecuteTask(DataAssetPermissions permissions);

}
