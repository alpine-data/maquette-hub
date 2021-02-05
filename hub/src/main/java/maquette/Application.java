package maquette;

import maquette.adapters.infrastructure.MaquetteMlflowProxyService;
import maquette.adapters.jdbc.JdbcJdbiImpl;
import maquette.adapters.MaquetteDataExplorer;
import maquette.adapters.collections.CollectionsRepositories;
import maquette.adapters.datasets.DatasetsRepositories;
import maquette.adapters.datasets.DatasetsStores;
import maquette.adapters.datasources.DataSourcesRepositories;
import maquette.adapters.infrastructure.InfrastructureProviders;
import maquette.adapters.infrastructure.InfrastructureRepositories;
import maquette.adapters.projects.ProjectsRepositories;
import maquette.adapters.sandboxes.SandboxesRepositories;
import maquette.adapters.streams.StreamsRepositories;
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

      var collectionsRepository = CollectionsRepositories.create(om);
      var datasetsRepository = DatasetsRepositories.create(om);
      var dataSourcesRepository = DataSourcesRepositories.create(om);
      var streamsRepository = StreamsRepositories.create(om);

      var datasetsStore = DatasetsStores.create();
      var sandboxesRepository = SandboxesRepositories.create(om);
      var usersRepository = UsersRepositories.create(om);

      var dataExplorer = MaquetteDataExplorer.apply(om);
      var mlflowProxyPort = MaquetteMlflowProxyService.apply(om);
      var jdbcPort = JdbcJdbiImpl.apply();

      CoreApp.apply(
         config, infrastructureProvider, infrastructureRepository, projectsRepository,
         collectionsRepository, datasetsRepository, datasetsStore, dataSourcesRepository, streamsRepository,
         sandboxesRepository, usersRepository, dataExplorer, mlflowProxyPort, jdbcPort, om);
   }

}
