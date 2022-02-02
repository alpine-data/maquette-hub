package maquette.development.services;

import maquette.core.modules.users.UserEntities;
import maquette.development.entities.SandboxEntities;
import maquette.development.ports.DataAssetsServicePort;
import maquette.development.entities.WorkspaceEntities;

public final class WorkspaceServicesFactory {

    private WorkspaceServicesFactory() {

    }

    public static WorkspaceServices createWorkspaceServices(WorkspaceEntities workspaces, DataAssetsServicePort provider, SandboxEntities sandboxes) {
        var companion = WorkspaceServicesCompanion.apply(workspaces, sandboxes);
        var impl = WorkspaceServicesImpl.apply(workspaces, sandboxes, provider);
        var secured = WorkspaceServicesSecured.apply(impl, companion);
        return WorkspaceServicesValidated.apply(secured);
    }

    public static SandboxServices createSandboxServices(WorkspaceEntities workspaces, DataAssetsServicePort provider, SandboxEntities sandboxes, UserEntities users) {
        var workspacesCompanion = WorkspaceServicesCompanion.apply(workspaces, sandboxes);
        var workspaceServices = WorkspaceServicesImpl.apply(workspaces, sandboxes, provider);

        var impl = SandboxServicesImpl.apply(sandboxes, workspaces, workspaceServices, users);
        var secured = SandboxServicesSecured.apply(impl, workspacesCompanion);
        return SandboxServicesValidated.apply(secured);
    }

}
