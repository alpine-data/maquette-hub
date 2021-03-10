package maquette.core.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.configuration.ConfigurationServices;
import maquette.core.services.configuration.ConfigurationServicesFactory;
import maquette.core.services.data.collections.CollectionServices;
import maquette.core.services.data.collections.CollectionServicesFactory;
import maquette.core.services.data.datasets.DatasetServices;
import maquette.core.services.data.datasets.DatasetServicesFactory;
import maquette.core.services.data.datasources.DataSourceServices;
import maquette.core.services.data.datasources.DataSourceServicesFactory;
import maquette.core.services.data.streams.StreamServices;
import maquette.core.services.data.streams.StreamServicesFactory;
import maquette.core.services.dependencies.DependencyServices;
import maquette.core.services.dependencies.DependencyServicesFactory;
import maquette.core.services.projects.ProjectServices;
import maquette.core.services.projects.ProjectServicesFactory;
import maquette.core.services.sandboxes.SandboxServices;
import maquette.core.services.sandboxes.SandboxServicesFactory;
import maquette.core.services.users.UserServices;
import maquette.core.services.users.UserServicesFactory;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class ApplicationServices {

    DependencyServices dependencyServices;

    ConfigurationServices configurationServices;

    ProcessServices processServices;

    ProjectServices projectServices;

    CollectionServices collectionServices;

    DatasetServices datasetServices;

    DataSourceServices dataSourceServices;

    StreamServices streamServices;

    SandboxServices sandboxServices;

    UserServices userServices;

    public static ApplicationServices apply(RuntimeConfiguration runtime) {
        var projectServices = ProjectServicesFactory.apply(
           runtime.getProcessManager(),
           runtime.getProjects(),
           runtime.getDatasets(),
           runtime.getDataSources(),
           runtime.getInfrastructureManager(),
           runtime.getSandboxes());

        var processServices = ProcessServicesImpl.apply(runtime.getProcessManager());
        var userServices = UserServicesFactory.apply(runtime.getProjects(), runtime.getCollections(), runtime.getDatasets(), runtime.getDataSources(), runtime.getStreams(), runtime.getUsers());
        var sandboxServices = SandboxServicesFactory.apply(runtime.getProcessManager(), runtime.getInfrastructureManager(), runtime.getProjects(), runtime.getSandboxes(), runtime.getDatasets());
        var collectionServices = CollectionServicesFactory.apply(runtime);
        var datasetServices = DatasetServicesFactory.apply(runtime);
        var dataSourceServices = DataSourceServicesFactory.apply(runtime);
        var streamServices = StreamServicesFactory.apply(runtime);
        var configurationServices = ConfigurationServicesFactory.apply(runtime.getUsers());

        var dependencyServices = DependencyServicesFactory.apply(
           runtime.getDependencies(), runtime.getProjects(), runtime.getDatasets(),
           runtime.getCollections(), runtime.getDataSources(), runtime.getStreams(), runtime.getUsers());

        return apply(
           dependencyServices, configurationServices, processServices, projectServices, collectionServices,
           datasetServices, dataSourceServices, streamServices, sandboxServices, userServices);
    }

}
