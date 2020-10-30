package test.itest;

import maquette.adapters.datasets.InMemoryDatasetsRepository;
import maquette.adapters.infrastructure.InMemoryInfrastructureRepository;
import maquette.adapters.infrastructure.InfrastructureProviders;
import maquette.adapters.projects.InMemoryProjectsRepository;
import maquette.adapters.users.InMemoryUsersRepository;
import maquette.common.ObjectMapperFactory;
import maquette.core.CoreApp;
import maquette.core.config.ApplicationConfiguration;
import maquette.core.values.user.AnonymousUser;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class ApplicationTest {

   private CoreApp app;

   @Before
   public void before() {
      var config = ApplicationConfiguration.apply();
      var om = ObjectMapperFactory.apply().create(true);

      var infrastructureProvider = InfrastructureProviders.create();
      var infrastructureRepository = InMemoryInfrastructureRepository.apply();
      var projectsRepository = InMemoryProjectsRepository.apply();
      var datasetsRepository = InMemoryDatasetsRepository.apply();
      var usersRepository = InMemoryUsersRepository.apply();

      app = CoreApp.apply(config, infrastructureProvider, infrastructureRepository, projectsRepository, datasetsRepository, usersRepository, om);
   }

   @Test
   public void test() throws ExecutionException, InterruptedException {
      var anonymous = AnonymousUser.apply();
      var result = app.getServices().getProcessServices().getAll(anonymous).toCompletableFuture().get();

      assert(result.size() == 0);
   }

}
