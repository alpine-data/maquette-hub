package maquette.core.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.data.datasets.DatasetServices;
import maquette.core.services.data.datasets.DatasetServicesFactory;
import maquette.core.services.data.datasources.DataSourceServices;
import maquette.core.services.data.datasources.DataSourceServicesFactory;
import maquette.core.services.projects.ProjectServices;
import maquette.core.services.projects.ProjectServicesFactory;
import maquette.core.services.sandboxes.SandboxServices;
import maquette.core.services.sandboxes.SandboxServicesFactory;
import maquette.core.services.users.UserServices;
import maquette.core.services.users.UserServicesFactory;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class ApplicationServices {

    ProcessServices processServices;

    ProjectServices projectServices;

    DatasetServices datasetServices;

    DataSourceServices dataSourceServices;

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
        var userServices = UserServicesFactory.apply(runtime.getProjects(), runtime.getDatasets(), runtime.getDataSources(), runtime.getUsers());
        var sandboxServices = SandboxServicesFactory.apply(runtime.getProcessManager(), runtime.getInfrastructureManager(), runtime.getProjects(), runtime.getSandboxes(), runtime.getDatasets());
        var datasetServices = DatasetServicesFactory.apply(runtime.getProjects(), runtime.getDatasets(), runtime.getProcessManager());
        var dataSourceServices = DataSourceServicesFactory.apply(runtime.getDataSources(), runtime.getProjects());

        return apply(processServices, projectServices, datasetServices, dataSourceServices, sandboxServices, userServices);
    }

}
