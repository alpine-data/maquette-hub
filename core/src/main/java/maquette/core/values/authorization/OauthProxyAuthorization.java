package maquette.core.values.authorization;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.values.user.OauthProxyUser;
import maquette.core.values.user.User;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class OauthProxyAuthorization implements Authorization {

    String name;

    @Override
    public boolean authorizes(User user) {
        return user instanceof OauthProxyUser && ((OauthProxyUser) user)
            .getName()
            .equals(this.name);
    }

    @Override
    public GenericAuthorizationDefinition toGenericAuthorizationDefinition() {
        return GenericAuthorizationDefinition.apply("oauth", name);
    }

}
