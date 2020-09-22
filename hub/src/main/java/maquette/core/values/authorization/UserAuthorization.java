package maquette.core.values.authorization;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

@Value
@AllArgsConstructor(staticName = "apply")
public class UserAuthorization implements Authorization {

    String userId;

    @Override
    public boolean isAuthorized(User user) {
        return user instanceof AuthenticatedUser && ((AuthenticatedUser) user).getId().equals(userId);
    }

}
