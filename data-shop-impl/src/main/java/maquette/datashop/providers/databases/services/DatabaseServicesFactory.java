package maquette.datashop.providers.databases.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.users.UserModule;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.ports.WorkspacesServicePort;
import maquette.datashop.providers.databases.DatabaseEntities;
import maquette.datashop.services.DataAssetServicesCompanion;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatabaseServicesFactory {

    public static DatabaseServices apply(MaquetteRuntime runtime, WorkspacesServicePort workspaces,
                                         DatabaseEntities databases) {
        var users = runtime
            .getModule(UserModule.class)
            .getUsers();
        var dataAssets = runtime
            .getModule(MaquetteDataShop.class)
            .getEntities();

        var comp = DataAssetServicesCompanion.apply(dataAssets, workspaces);
        var impl = DatabaseServicesImpl.apply(dataAssets, users, databases);

        return DatabaseServicesSecured.apply(dataAssets, impl, comp);
    }

}
