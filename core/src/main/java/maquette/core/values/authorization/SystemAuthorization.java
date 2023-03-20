package maquette.core.values.authorization;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.values.user.User;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class SystemAuthorization implements Authorization {

    String name;

    @Override
    public boolean authorizes(User user) {
        return false;
    }

    @Override
    public GenericAuthorizationDefinition toGenericAuthorizationDefinition() {
        return GenericAuthorizationDefinition.apply("system", name);
    }

}
