package maquette.core.services.data;

import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.assets.DataAssetEntities;
import maquette.core.entities.data.assets.DataAssetEntity;
import maquette.core.entities.logs.Logs;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.services.logs.LogsCompanion;
import maquette.core.values.data.DataAssetProperties;

public final class DataAssetServicesFactory {

   private DataAssetServicesFactory() {

   }

   public static <P extends DataAssetProperties<P>, E extends DataAssetEntity<P>, EN extends DataAssetEntities<P, E>> DataAssetServices<P, E> apply(
      EN assets, RuntimeConfiguration runtime) {
      var comp = DataAssetCompanion.<P, EN>apply(assets, runtime.getProjects());
      var logs = LogsCompanion.apply(runtime.getLogs(), runtime);
      var impl = DataAssetServicesImpl.<P, E, EN>apply(assets, runtime.getProjects(), comp, logs);
      return DataAssetServicesSecured.apply(impl, comp, assets);
   }

}
