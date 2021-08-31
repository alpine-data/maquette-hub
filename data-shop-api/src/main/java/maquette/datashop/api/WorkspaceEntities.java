package maquette.datashop.api;

import maquette.core.values.UID;

import java.util.concurrent.CompletionStage;

public interface WorkspaceEntities {

   CompletionStage<WorkspaceEntity> getWorkspaceByName(String name);

   CompletionStage<WorkspaceEntity> getWorkspaceById(UID id);

}
