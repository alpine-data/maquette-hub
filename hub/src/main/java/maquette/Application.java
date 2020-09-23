package maquette;

import maquette.adapters.infrastructure.DockerInfrastructureProvider;
import maquette.adapters.infrastructure.InfrastructureRepositories;
import maquette.adapters.projects.ProjectsRepositories;
import maquette.common.ObjectMapperFactory;
import maquette.core.CoreApp;
import maquette.core.config.ApplicationConfiguration;

public class Application {

    public static void main(String[] args) {
        var config = ApplicationConfiguration.apply();
        var om = ObjectMapperFactory.apply().create(true);

        var infrastructureProvider = DockerInfrastructureProvider.apply();
        var infrastructureRepository = InfrastructureRepositories.create(om);
        var projectsRepository = ProjectsRepositories.create(om);

        CoreApp.apply(config, infrastructureProvider, infrastructureRepository, projectsRepository, om);
    }

}
