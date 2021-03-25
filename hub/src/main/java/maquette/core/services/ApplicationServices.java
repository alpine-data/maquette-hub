package maquette.core.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.configuration.ConfigurationServices;
import maquette.core.services.configuration.ConfigurationServicesFactory;
import maquette.core.services.data.DataAssetServices;
import maquette.core.services.data.DataAssetServicesFactory;
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

    DataAssetServices dataAssetServices;

    ProcessServices processServices;

    ProjectServices projectServices;

    SandboxServices sandboxServices;

    UserServices userServices;

    public static ApplicationServices apply(RuntimeConfiguration runtime) {
        var projectServices = ProjectServicesFactory.apply(runtime);
        var processServices = ProcessServicesImpl.apply(runtime.getProcessManager());
        var userServices = UserServicesFactory.apply(runtime);
        var sandboxServices = SandboxServicesFactory.apply(runtime);
        var configurationServices = ConfigurationServicesFactory.apply(runtime.getUsers());
        var dataAssetServices = DataAssetServicesFactory.apply(runtime);
        var dependencyServices = DependencyServicesFactory.apply(runtime);

        return apply(
           dependencyServices, configurationServices, dataAssetServices,
           processServices, projectServices, sandboxServices, userServices);
    }

}
