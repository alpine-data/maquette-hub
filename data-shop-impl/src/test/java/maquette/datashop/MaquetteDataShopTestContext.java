package maquette.datashop;

import lombok.AllArgsConstructor;
import maquette.datashop.entities.DataAssetEntities;
import maquette.datashop.ports.InMemoryDataAssetsRepository;
import maquette.datashop.providers.DataAssetProviders;
import maquette.datashop.providers.FakeProvider;
import maquette.datashop.services.DataAssetServicesFactory;
import maquette.workspaces.api.WorkspaceEntities;
import maquette.workspaces.fake.FakeWorkspaceEntities;

@AllArgsConstructor(staticName = "apply")
public final class MaquetteDataShopTestContext {

    WorkspaceEntities workspaces;

    MaquetteDataShop shop;

    public static MaquetteDataShopTestContext apply() {
        var assets = DataAssetEntities.apply(InMemoryDataAssetsRepository.apply(), DataAssetProviders.apply(FakeProvider
            .apply()));
        var workspaces = FakeWorkspaceEntities.apply();
        var providers = DataAssetProviders.apply(FakeProvider.apply());

        var services = DataAssetServicesFactory.apply(assets, workspaces, providers);
        var shop = MaquetteDataShop.apply(services, providers);

        return apply(workspaces, shop);
    }

    public CustomSettings createCustomSettings() {
        return CustomSettings.apply();
    }

    @AllArgsConstructor(staticName = "apply")
    public static class CustomSettings {

    }

}
