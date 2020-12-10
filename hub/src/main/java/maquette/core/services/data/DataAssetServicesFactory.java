package maquette.core.services.data;

import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.data.DataAssetEntity;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.values.data.DataAssetProperties;

public final class DataAssetServicesFactory {

   private DataAssetServicesFactory() {

   }

   public static <P extends DataAssetProperties<P>, E extends DataAssetEntity<P>, EN extends DataAssetEntities<P, E>> DataAssetServices<P, E> apply(EN assets, ProjectEntities projects) {
      var comp = DataAssetCompanion.<P, EN>apply(assets, projects);
      var impl = DataAssetServicesImpl.<P, E, EN>apply(assets, projects, comp);
      return DataAssetServicesSecured.apply(impl, comp);
   }

}
