package maquette.core.modules.users.services;

import maquette.core.modules.users.UserEntities;

public final class UserServicesFactory {

    private UserServicesFactory() {

    }

    public static UserServices apply(UserEntities users) {
        var comp = UserCompanion.apply(users);
        var impl = UserServicesImpl.apply(users, comp);
        var validate = UserServicesValidated.apply(impl);
        var secured = UserServicesSecured.apply(comp, validate);
        return secured;
    }

}
