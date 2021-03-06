package maquette.core.values.authorization;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.user.User;

@Value
@AllArgsConstructor(staticName = "apply")
public class WildcardAuthorization implements Authorization {

    @Override
    public boolean authorizes(User user) {
        return true;
    }

    @Override
    public String getName() {
        return "*";
    }

    @Override
    public GenericAuthorizationDefinition toGenericAuthorizationDefinition() {
        return GenericAuthorizationDefinition.apply("*");
    }

}
