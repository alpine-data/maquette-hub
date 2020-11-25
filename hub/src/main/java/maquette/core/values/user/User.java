package maquette.core.values.user;

import maquette.core.values.authorization.Authorization;

import java.util.List;

public interface User {

    String getDisplayName();

    List<String> getRoles();

    Authorization toAuthorization();

}
