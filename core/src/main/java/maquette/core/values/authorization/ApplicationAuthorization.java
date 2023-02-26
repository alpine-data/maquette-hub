package maquette.core.values.authorization;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.values.user.ApplicationUser;
import maquette.core.values.user.User;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ApplicationAuthorization implements Authorization {

    String name;

    @Override
    public boolean authorizes(User user) {
        return user instanceof ApplicationUser && ((ApplicationUser) user)
            .getId()
            .getValue()
            .equals(this.name);
    }

    @Override
    public GenericAuthorizationDefinition toGenericAuthorizationDefinition() {
        return GenericAuthorizationDefinition.apply("application", name);
    }

}
