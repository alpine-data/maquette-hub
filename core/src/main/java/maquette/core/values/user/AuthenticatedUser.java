package maquette.core.values.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;

import java.util.Arrays;
import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class AuthenticatedUser implements User {

    String id;

    List<String> roles;

    public static AuthenticatedUser apply(String id, String ...roles) {
        return apply(id, Arrays.asList(roles));
    }

    @Override
    public String getDisplayName() {
        return id;
    }

    @Override
    public Authorization toAuthorization() {
        return UserAuthorization.apply(id);
    }

    public AuthenticatedUser withRoles(List<String> roles) {
        return apply(id, roles);
    }

    public AuthenticatedUser withRoles(String ... roles) {
        return apply(id, roles);
    }

}
