package maquette.core.entities.data.datasets.model.tasks;

import maquette.core.values.data.DataAssetPermissions;

public interface Task {

   boolean canExecuteTask(DataAssetPermissions permissions);

}
