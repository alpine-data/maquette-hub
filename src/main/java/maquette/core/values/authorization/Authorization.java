package maquette.core.values.authorization;

import maquette.core.values.user.User;

public interface Authorization {

    boolean isAuthorized(User user);

}
