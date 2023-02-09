package maquette.development.services;

import maquette.core.modules.users.UserEntities;
import maquette.core.modules.users.services.UserServicesFactory;
import maquette.development.entities.SandboxEntities;
import maquette.development.entities.WorkspaceEntities;
import maquette.development.ports.DataAssetsServicePort;

public final class WorkspaceServicesFactory {

    private WorkspaceServicesFactory() {

    }

    public static WorkspaceServices createWorkspaceServices(WorkspaceEntities workspaces,
                                                            DataAssetsServicePort provider,
                                                            SandboxEntities sandboxes) {
        var companion = WorkspaceServicesCompanion.apply(workspaces, sandboxes);
        var impl = WorkspaceServicesImpl.apply(workspaces, sandboxes, provider);
        var secured = WorkspaceServicesSecured.apply(impl, companion);
        return WorkspaceServicesValidated.apply(secured);
    }

    public static SandboxServices createSandboxServices(WorkspaceEntities workspaces, DataAssetsServicePort provider,
                                                        SandboxEntities sandboxes, UserEntities users) {
        var workspacesCompanion = WorkspaceServicesCompanion.apply(workspaces, sandboxes);
        var workspaceServices = WorkspaceServicesImpl.apply(workspaces, sandboxes, provider);
        var userServices = UserServicesFactory.apply(users);

        var impl = SandboxServicesImpl.apply(sandboxes, workspaces, users);
        var secured = SandboxServicesSecured.apply(impl, userServices, workspacesCompanion);
        return SandboxServicesValidated.apply(secured);
    }

}
