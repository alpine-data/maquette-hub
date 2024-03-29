package maquette.datashop.providers.datasets.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.users.UserModule;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.ports.WorkspacesServicePort;
import maquette.datashop.providers.datasets.ports.DatasetDataExplorer;
import maquette.datashop.providers.datasets.ports.DatasetsRepository;
import maquette.datashop.services.DataAssetServicesCompanion;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatasetServicesFactory {

    public static DatasetServices apply(MaquetteRuntime runtime, DatasetsRepository repository,
                                        DatasetDataExplorer dataExplorer, WorkspacesServicePort workspaces) {
        var users = runtime
            .getModule(UserModule.class)
            .getUsers();
        var shop = runtime.getModule(MaquetteDataShop.class);
        var comp = DataAssetServicesCompanion.apply(shop.getEntities(), workspaces);
        var impl = DatasetServicesImpl.apply(repository, dataExplorer, shop.getEntities(), users);
        return DatasetServicesSecured.apply(impl, comp);
    }

}
