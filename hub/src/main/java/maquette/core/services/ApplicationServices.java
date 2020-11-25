package maquette.core.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.datasets.DatasetServices;
import maquette.core.services.datasets.DatasetServicesFactory;
import maquette.core.services.projects.ProjectServices;
import maquette.core.services.projects.ProjectServicesFactory;
import maquette.core.services.sandboxes.SandboxServices;
import maquette.core.services.sandboxes.SandboxServicesFactory;
import maquette.core.services.sandboxes.SandboxServicesImpl;
import maquette.core.services.users.UserServices;
import maquette.core.services.users.UserServicesFactory;
import maquette.core.services.users.UserServicesImpl;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class ApplicationServices {

    ProcessServices processServices;

    ProjectServices projectServices;

    DatasetServices datasetServices;

    SandboxServices sandboxServices;

    UserServices userServices;

    public static ApplicationServices apply(RuntimeConfiguration runtime) {
        var projectServices = ProjectServicesFactory.apply(runtime.getProcessManager(), runtime.getProjects(), runtime.getDatasets(), runtime.getInfrastructureManager());
        var processServices = ProcessServicesImpl.apply(runtime.getProcessManager());
        var userServices = UserServicesFactory.apply(runtime.getProjects(), runtime.getUsers());
        var sandboxServices = SandboxServicesFactory.apply(runtime.getProcessManager(), runtime.getInfrastructureManager(), runtime.getProjects(), runtime.getSandboxes(), runtime.getDatasets());
        var datasetServices = DatasetServicesFactory.apply(runtime.getProjects(), runtime.getDatasets());

        return apply(processServices, projectServices, datasetServices, sandboxServices, userServices);
    }

}
