package maquette.development.services;

import maquette.development.configuration.StacksConfiguration;
import maquette.development.entities.SandboxEntities;
import maquette.development.entities.WorkspaceEntities;
import maquette.development.ports.InMemorySandboxesRepository;
import maquette.development.ports.InMemoryWorkspacesRepository;
import maquette.development.ports.infrastructure.FakeInfrastructurePort;
import maquette.development.ports.infrastructure.InfrastructurePort;

public class CentralModelRegistryFactory {

    public static CentralModelRegistryServices createCentralModelRegistryServices(
        WorkspaceEntities cmrWorkspaces,
        WorkspaceEntities workspaces,
        InfrastructurePort infrastructurePort
    ) {
        var fakeSandboxes = SandboxEntities.apply(
            InMemoryWorkspacesRepository.apply(),
            InMemorySandboxesRepository.apply(),
            FakeInfrastructurePort.apply(),
            StacksConfiguration.apply());
        var cmrCompanion = WorkspaceServicesCompanion.apply(cmrWorkspaces, fakeSandboxes);
        var companion = WorkspaceServicesCompanion.apply(workspaces, fakeSandboxes);
        var impl = CentralModelRegistryServicesImpl.apply(cmrWorkspaces, workspaces, infrastructurePort);
        var secured = CentralModelRegistryServicesSecured.apply(impl, cmrCompanion, companion);
        return CentralModelRegistryServicesValidated.apply(secured);
    }

}
