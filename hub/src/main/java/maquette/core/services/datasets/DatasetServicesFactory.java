package maquette.core.services.datasets;

import maquette.core.entities.data.datasets.Datasets;
import maquette.core.entities.projects.Projects;
import maquette.core.services.projects.ProjectCompanion;

public final class DatasetServicesFactory {

   private DatasetServicesFactory() {

   }

   public static DatasetServices apply(Projects projects, Datasets datasets) {
      var comp = DatasetCompanion.apply(projects, datasets);
      var projectCompanion = ProjectCompanion.apply(projects, datasets);
      var impl = DatasetServicesImpl.apply(datasets, comp);

      return DatasetServicesSecured.apply(impl, projectCompanion, comp);
   }

}
