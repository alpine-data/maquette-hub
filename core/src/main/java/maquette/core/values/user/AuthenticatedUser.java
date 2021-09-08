package maquette.core.values.user;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;

import java.util.Arrays;
import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class AuthenticatedUser implements User {

    UID id;

    List<String> roles;

    public static AuthenticatedUser apply(UID id, String... roles) {
        return apply(id, Arrays.asList(roles));
    }

    @Override
    public String getDisplayName() {
        return id.getValue();
    }

    @Override
    public Authorization toAuthorization() {
        return UserAuthorization.apply(id.getValue());
    }

    public AuthenticatedUser withRoles(List<String> roles) {
        return apply(id, roles);
    }

    public AuthenticatedUser withRoles(String... roles) {
        return apply(id, roles);
    }

}
