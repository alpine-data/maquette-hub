package maquette;

import maquette.adapters.MaquetteDataExplorer;
import maquette.adapters.collections.CollectionsRepositories;
import maquette.adapters.data.DataAssetsRepositories;
import maquette.adapters.datasets.DatasetsRepositories;
import maquette.adapters.infrastructure.InfrastructureProviders;
import maquette.adapters.infrastructure.InfrastructureRepositories;
import maquette.adapters.infrastructure.MaquetteMlflowProxyService;
import maquette.adapters.jdbc.JdbcJdbiImpl;
import maquette.adapters.projects.ApplicationsRepositories;
import maquette.adapters.projects.ModelsRepositories;
import maquette.adapters.projects.ProjectsRepositories;
import maquette.adapters.sandboxes.SandboxesRepositories;
import maquette.adapters.users.UsersRepositories;
import maquette.asset_providers.collections.Collections;
import maquette.asset_providers.datasets.Datasets;
import maquette.asset_providers.sources.DataSources;
import maquette.asset_providers.streams.Streams;
import maquette.common.ObjectMapperFactory;
import maquette.core.CoreApp;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.entities.data.DataAssetProviders;

public class Application {

   public static void main(String[] args) {
      var config = ApplicationConfiguration.apply();
      var om = ObjectMapperFactory.apply().create(true);

      var infrastructureProvider = InfrastructureProviders.create(om);
      var infrastructureRepository = InfrastructureRepositories.create(om);
      var projectsRepository = ProjectsRepositories.create(om);
      var modelsRepository = ModelsRepositories.create(om);
      var applicationsRepository = ApplicationsRepositories.create(om);
      var dataAssetsRepository = DataAssetsRepositories.create(om);

      var sandboxesRepository = SandboxesRepositories.create(om);
      var usersRepository = UsersRepositories.create(om);

      var dataExplorer = MaquetteDataExplorer.apply(om);
      var mlflowProxyPort = MaquetteMlflowProxyService.apply(om);

      var datasetsRepository = DatasetsRepositories.create(om);
      var datasets = Datasets.apply(datasetsRepository, dataExplorer);

      var collectionsRepository = CollectionsRepositories.create(om);
      var collections = Collections.apply(collectionsRepository);

      var jdbcPort = JdbcJdbiImpl.apply();
      var sources = DataSources.apply(jdbcPort, dataExplorer);

      var streams = Streams.apply();

      var dataAssetProviders = DataAssetProviders.apply(datasets, collections, sources, streams);

      CoreApp.apply(
         config,
         infrastructureProvider,
         infrastructureRepository,
         projectsRepository,
         modelsRepository,
         applicationsRepository,
         sandboxesRepository,
         usersRepository,
         dataAssetsRepository,
         dataAssetProviders,
         mlflowProxyPort,
         om);
   }

}
