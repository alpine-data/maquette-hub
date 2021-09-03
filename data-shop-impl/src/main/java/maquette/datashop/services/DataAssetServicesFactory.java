package maquette.datashop.services;

import maquette.workspaces.api.WorkspaceEntities;
import maquette.datashop.entities.DataAssetEntities;
import maquette.datashop.providers.DataAssetProviders;

/**
 * Factory class/ method to compose a new DataAssetServices instance.
 */
public final class DataAssetServicesFactory {

   /**
    * Creates a new service instance.
    *
    * @param dataAssets The underlying data asset entities.
    * @param workspaces Interface to workspace/ project management, required to link data access requests and to check user permissions to data assets.
    * @param providers A set of registered data asset providers.
    * @return The new instance.
    */
   public static DataAssetServices apply(DataAssetEntities dataAssets, WorkspaceEntities workspaces, DataAssetProviders providers) {
      var comp = DataAssetServicesCompanion.apply(dataAssets, workspaces);
      var impl =  DataAssetServicesImpl.apply(dataAssets, workspaces, providers);
      return DataAssetServicesSecured.apply(comp, impl);
   }

}
