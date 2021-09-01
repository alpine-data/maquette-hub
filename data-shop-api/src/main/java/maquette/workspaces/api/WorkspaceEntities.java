package maquette.workspaces.api;

import maquette.core.values.UID;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface WorkspaceEntities {

   CompletionStage<WorkspaceEntity> getWorkspaceByName(String name);

   CompletionStage<WorkspaceEntity> getWorkspaceById(UID id);

   CompletionStage<List<WorkspaceEntity>> getWorkspacesByMember(User user);

}
