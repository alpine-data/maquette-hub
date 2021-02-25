package maquette.core.services.dependencies;

import maquette.core.entities.data.collections.CollectionEntities;
import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasources.DataSourceEntities;
import maquette.core.entities.data.streams.StreamEntities;
import maquette.core.entities.dependencies.Dependencies;
import maquette.core.entities.projects.ProjectEntities;

public final class DependencyServicesFactory {

   private DependencyServicesFactory() {

   }

   public static DependencyServices apply(
      Dependencies dependencies,
      ProjectEntities projects,
      DatasetEntities datasets,
      CollectionEntities collections,
      DataSourceEntities dataSources,
      StreamEntities streams) {

      return DependencyServicesImpl.apply(
         dependencies,
         projects,
         datasets,
         collections,
         dataSources,
         streams);
   }

}
