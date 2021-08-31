package maquette.datashop.services;

import maquette.core.MaquetteRuntime;
import maquette.datashop.api.WorkspaceEntities;
import maquette.datashop.entities.DataAssetEntities;

public final class DataAssetServicesFactory {

   private DataAssetServicesFactory() {

   }

   public static DataAssetServices apply(MaquetteRuntime runtime, DataAssetEntities entities, WorkspaceEntities workspaces) {
      return DataAssetServicesImpl.apply(entities, workspaces);
   }

}
