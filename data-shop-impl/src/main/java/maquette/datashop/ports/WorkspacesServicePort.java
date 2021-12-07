package maquette.datashop.ports;

import com.fasterxml.jackson.databind.JsonNode;
import maquette.core.values.UID;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface WorkspacesServicePort {

    /**
     * Return a unique id of a workspace.
     *
     * @param workspaceName The name of the workspace.
     * @return The unique id.
     */
    CompletionStage<UID> getWorkspaceIdByName(String workspaceName);

    /**
     * Returns a list of workspaces the user is member of.
     *
     * @param user The user who should be member of the workspace.
     * @return A list of workspaces.
     */
    CompletionStage<List<Workspace>> getWorkspacesByMember(User user);

    /**
     * Return workspace properties (can be any information about the workspace, it will only be used to enrich
     * output to the user).
     *
     * @param id The unique id of the workspace.
     * @return A JSON describing the workspace.
     */
    CompletionStage<JsonNode> getWorkspacePropertiesByWorkspaceId(UID id);

}
