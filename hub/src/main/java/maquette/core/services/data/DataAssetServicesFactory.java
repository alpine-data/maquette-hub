package maquette.core.services.data;

import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.logs.LogsCompanion;

public final class DataAssetServicesFactory {

   private DataAssetServicesFactory() {

   }

   public static DataAssetServices apply(RuntimeConfiguration runtime) {
      var companion = DataAssetCompanion.apply(runtime);
      var logCompanion = LogsCompanion.apply(runtime.getLogs(), runtime);

      var impl = DataAssetServicesImpl.apply(runtime.getDataAssets(), runtime.getProjects(), companion, logCompanion);
      var secured = DataAssetServicesSecured.apply(impl, companion);
      var logged = DataAssetServicesLogged.apply(runtime.getDataAssets(), secured, runtime.getLogs());
      return DataAssetServicesValidated.apply(logged);
   }

}
