package maquette.core.values.authorization;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.values.user.User;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RoleAuthorization implements Authorization {

    String role;

    @Override
    public boolean authorizes(User user) {
        return user.getRoles().stream().anyMatch(roleId -> roleId.equals(this.role));
    }

    @Override
    public String getKey() {
        return "role:" + role;
    }

}
