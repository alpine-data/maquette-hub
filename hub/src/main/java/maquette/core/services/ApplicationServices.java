package maquette.core.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.config.RuntimeConfiguration;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class ApplicationServices {

    ProcessServices processServices;

    ProjectServices projectServices;

    DatasetServices datasetServices;

    UserServices userServices;

    public static ApplicationServices apply(RuntimeConfiguration runtime) {
        var projectServices = ProjectServicesImpl.apply(runtime.getProcessManager(), runtime.getProjects(), runtime.getInfrastructureManager());
        var processServices = ProcessServicesImpl.apply(runtime.getProcessManager());
        var userServices = UserServicesImpl.apply(runtime.getUsers());
        var datasetServices = DatasetServicesImpl.apply(runtime.getDatasets(), runtime.getProjects(), runtime.getUsers());

        return apply(processServices, projectServices, datasetServices, userServices);
    }

}
