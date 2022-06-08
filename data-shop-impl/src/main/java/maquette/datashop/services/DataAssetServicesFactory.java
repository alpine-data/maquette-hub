package maquette.datashop.services;

import maquette.core.ports.email.EmailClient;
import maquette.datashop.configuration.DataShopConfiguration;
import maquette.datashop.entities.DataAssetEntities;
import maquette.datashop.ports.WorkspacesServicePort;
import maquette.datashop.providers.DataAssetProviders;

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
    public static DataAssetServices apply(DataShopConfiguration configuration, DataAssetEntities dataAssets,
                                          WorkspacesServicePort workspaces,
                                          DataAssetProviders providers, EmailClient emailClient) {
        var comp = DataAssetServicesCompanion.apply(dataAssets, workspaces);
        var impl = DataAssetServicesImpl.apply(dataAssets, workspaces, providers, comp, emailClient);
        var secured = DataAssetServicesSecured.apply(comp, impl);
        return DataAssetServicesValidated.apply(secured, configuration);
    }

}
