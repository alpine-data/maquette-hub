package maquette.core.values.authorization;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.user.User;

@Value
@AllArgsConstructor(staticName = "apply")
public class WildcardAuthorization implements Authorization {

    @Override
    public boolean isAuthorized(User user) {
        return true;
    }

}
