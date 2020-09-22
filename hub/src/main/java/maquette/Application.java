package maquette;

import maquette.adapters.infrastructure.DockerInfrastructureProvider;
import maquette.adapters.infrastructure.InMemoryInfrastructureRepository;
import maquette.adapters.projects.InMemoryProjectsRepository;
import maquette.core.CoreApp;
import maquette.core.config.ApplicationConfiguration;

public class Application {

    public static void main(String[] args) {
        var config = ApplicationConfiguration.apply();

        var infrastructureProvider = DockerInfrastructureProvider.apply();
        var infrastructureRepository = InMemoryInfrastructureRepository.apply();
        var projectsRepository = InMemoryProjectsRepository.apply();

        CoreApp.apply(config, infrastructureProvider, infrastructureRepository, projectsRepository);
    }

}
