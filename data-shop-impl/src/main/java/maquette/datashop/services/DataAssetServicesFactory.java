package maquette.datashop.services;

import maquette.datashop.configuration.DataShopConfiguration;
import maquette.datashop.entities.DataAssetEntities;
import maquette.datashop.providers.DataAssetProviders;
import maquette.workspaces.api.WorkspaceEntities;

/**
 * Factory class/ method to compose a new DataAssetServices instance.
 */
public final class DataAssetServicesFactory {

    /**
     * Creates a new service instance.
     *
     * @param dataAssets The underlying data asset entities.
     * @param workspaces Interface to workspace/ project management, required to link data access requests and to
     *                   check user permissions to data assets.
     * @param providers  A set of registered data asset providers.
     * @return The new instance.
     */
    public static DataAssetServices apply(DataShopConfiguration configuration, DataAssetEntities dataAssets, WorkspaceEntities workspaces,
                                          DataAssetProviders providers) {
        var comp = DataAssetServicesCompanion.apply(dataAssets, workspaces);
        var impl = DataAssetServicesImpl.apply(dataAssets, workspaces, providers);
        var secured = DataAssetServicesSecured.apply(comp, impl);
        return DataAssetServicesValidated.apply(secured, configuration);
    }

}
