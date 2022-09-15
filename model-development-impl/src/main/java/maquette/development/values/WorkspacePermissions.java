package maquette.development.values;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.user.User;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class WorkspacePermissions {

    boolean admin;

    boolean member;

    public static WorkspacePermissions forUser(User executor, List<GrantedAuthorization<WorkspaceMemberRole>> members) {
        var isAdmin = members
            .stream()
            .anyMatch(m -> m
                .getRole()
                .equals(WorkspaceMemberRole.ADMIN) && m
                .getAuthorization()
                .authorizes(executor));
        var isMember = members
            .stream()
            .anyMatch(m -> m
                .getRole()
                .equals(WorkspaceMemberRole.MEMBER) && m
                .getAuthorization()
                .authorizes(executor));

        return apply(isAdmin, isMember);
    }

    @JsonProperty
    public boolean canChangeSettings() {
        return admin;
    }

    @JsonProperty
    public boolean canManageAllSandboxes() {
        return admin;
    }

}
