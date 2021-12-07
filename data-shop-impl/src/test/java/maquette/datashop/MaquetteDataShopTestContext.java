package maquette.datashop;

import lombok.AllArgsConstructor;
import maquette.datashop.configuration.DataShopConfiguration;
import maquette.datashop.entities.DataAssetEntities;
import maquette.datashop.ports.InMemoryDataAssetsRepository;
import maquette.datashop.providers.DataAssetProviders;
import maquette.datashop.providers.FakeProvider;
import maquette.datashop.services.DataAssetServicesFactory;
import maquette.datashop.ports.WorkspacesServicePort;
import maquette.datashop.ports.FakeWorkspacesServicePort;

@AllArgsConstructor(staticName = "apply")
public final class MaquetteDataShopTestContext {

    WorkspacesServicePort workspaces;

    MaquetteDataShop shop;

    public static MaquetteDataShopTestContext apply() {
        var assets = DataAssetEntities.apply(
            InMemoryDataAssetsRepository.apply(),
            DataAssetProviders.apply(FakeProvider.apply()));
        var workspaces = FakeWorkspacesServicePort.apply();
        var providers = DataAssetProviders.apply(FakeProvider.apply());
        var configuration = DataShopConfiguration.apply();

        var services = DataAssetServicesFactory.apply(configuration, assets, workspaces, providers);
        var shop = MaquetteDataShop.apply(services, providers, assets, configuration);

        return apply(workspaces, shop);
    }

    public CustomSettings createCustomSettings() {
        return CustomSettings.apply();
    }

    @AllArgsConstructor(staticName = "apply")
    public static class CustomSettings {

    }

}
