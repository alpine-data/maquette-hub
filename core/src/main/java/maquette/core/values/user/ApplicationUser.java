package maquette.core.values.user;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.UID;
import maquette.core.values.authorization.ApplicationAuthorization;
import maquette.core.values.authorization.Authorization;

import java.util.Arrays;
import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class ApplicationUser implements User {

    UID id;

    List<String> roles;

    public static ApplicationUser apply(UID id, String... roles) {
        return apply(id, Arrays.asList(roles));
    }

    @Override
    public String getDisplayName() {
        return id.getValue();
    }

    @Override
    public Authorization toAuthorization() {
        return ApplicationAuthorization.apply(id.getValue());
    }

    public ApplicationUser withRoles(List<String> roles) {
        return apply(id, roles);
    }

    public ApplicationUser withRoles(String... roles) {
        return apply(id, roles);
    }

}
