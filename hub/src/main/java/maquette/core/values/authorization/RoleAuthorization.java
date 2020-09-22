package maquette.core.values.authorization;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.user.User;

@Value
@AllArgsConstructor(staticName = "apply")
public class RoleAuthorization implements Authorization {

    String roleId;

    @Override
    public boolean isAuthorized(User user) {
        return user.getRoles().stream().anyMatch(roleId -> roleId.equals(this.roleId));
    }

}
