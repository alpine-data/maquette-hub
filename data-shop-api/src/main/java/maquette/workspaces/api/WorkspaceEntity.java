package maquette.workspaces.api;

import maquette.core.values.UID;

import java.util.concurrent.CompletionStage;

public interface WorkspaceEntity {

   UID getId();

   CompletionStage<WorkspaceProperties> getProperties();

}
