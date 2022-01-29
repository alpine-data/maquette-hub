package maquette.development.values;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.user.User;
import maquette.development.values.sandboxes.Sandbox;
import maquette.development.values.sandboxes.SandboxProperties;
import maquette.development.values.stacks.Stack;
import maquette.development.values.stacks.StackProperties;

import java.util.List;
import java.util.Objects;

@Value
@With
@AllArgsConstructor(staticName = "apply")
public class Workspace {

    WorkspaceProperties properties;

    List<JsonNode> dataAccessRequests;

    List<GrantedAuthorization<WorkspaceMemberRole>> members;

    List<JsonNode> assets;

    List<Sandbox> sandboxes;

    public boolean isMember(User user, WorkspaceMemberRole role) {
        return members
           .stream()
           .anyMatch(granted -> granted.getAuthorization().authorizes(user) && (Objects.isNull(role) || granted.getRole().equals(role)));
    }

    public boolean isMember(User user) {
        return isMember(user, null);
    }

    public WorkspacePermissions getWorkspacePermissions(User user) {
        return WorkspacePermissions.forUser(user, members);
    }

}
