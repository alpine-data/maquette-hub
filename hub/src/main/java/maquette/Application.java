package maquette;

import maquette.adapters.datasets.DatasetsRepositories;
import maquette.adapters.datasets.DatasetsStores;
import maquette.adapters.infrastructure.InfrastructureProviders;
import maquette.adapters.infrastructure.InfrastructureRepositories;
import maquette.adapters.projects.ProjectsRepositories;
import maquette.adapters.users.UsersRepositories;
import maquette.common.ObjectMapperFactory;
import maquette.core.CoreApp;
import maquette.core.config.ApplicationConfiguration;

public class Application {

   public static void main(String[] args) {
      var config = ApplicationConfiguration.apply();
      var om = ObjectMapperFactory.apply().create(true);

      var infrastructureProvider = InfrastructureProviders.create();
      var infrastructureRepository = InfrastructureRepositories.create(om);
      var projectsRepository = ProjectsRepositories.create(om);
      var datasetsRepository = DatasetsRepositories.create(om);
      var datasetsStore = DatasetsStores.create();
      var usersRepository = UsersRepositories.create(om);

      CoreApp.apply(config, infrastructureProvider, infrastructureRepository, projectsRepository, datasetsRepository, datasetsStore, usersRepository, om);
   }

}
