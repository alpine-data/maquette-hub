package maquette.datashop.services;

import maquette.core.MaquetteRuntime;
import maquette.datashop.entities.DataAssetEntities;

public final class DataAssetServicesFactory {

   private DataAssetServicesFactory() {

   }

   public static DataAssetServices apply(MaquetteRuntime runtime, DataAssetEntities entities) {
      return DataAssetServicesImpl.apply(entities);
   }

}
