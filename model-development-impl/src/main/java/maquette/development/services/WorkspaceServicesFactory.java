package maquette.development.services;

import maquette.development.entities.SandboxEntities;
import maquette.development.ports.DataAssetsServicePort;
import maquette.development.entities.WorkspaceEntities;

public final class WorkspaceServicesFactory {

    private WorkspaceServicesFactory() {

    }

    public static WorkspaceServices createWorkspaceServices(WorkspaceEntities workspaces, DataAssetsServicePort provider) {
        var companion = WorkspaceServicesCompanion.apply(workspaces);
        var impl = WorkspaceServicesImpl.apply(workspaces, provider);
        var secured = WorkspaceServicesSecured.apply(impl, companion);
        return WorkspaceServicesValidated.apply(secured);
    }

    public static SandboxServices createSandboxServices(WorkspaceEntities workspaces, DataAssetsServicePort provider, SandboxEntities sandboxes) {
        var companion = WorkspaceServicesCompanion.apply(workspaces);
        var workspacesCompanion = WorkspaceServicesCompanion.apply(workspaces);
        var workspaceServices = WorkspaceServicesImpl.apply(workspaces, provider);

        var impl = SandboxServicesImpl.apply(sandboxes, workspaces, workspaceServices);
        var secured = SandboxServicesSecured.apply(impl, workspacesCompanion);
        return SandboxServicesValidated.apply(secured);
    }

}
