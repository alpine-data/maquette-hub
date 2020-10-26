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

    public static ApplicationServices apply(RuntimeConfiguration runtime) {
        var projectServices = ProjectServicesImpl.apply(runtime.getProcessManager(), runtime.getProjects(), runtime.getInfrastructureManager());
        var processServices = ProcessServicesImpl.apply(runtime.getProcessManager());
        var datasetServices = DatasetServicesImpl.apply(runtime.getDatasets(), runtime.getProjects());

        return apply(processServices, projectServices, datasetServices);
    }

}
