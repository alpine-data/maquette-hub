package maquette.development.services;

import maquette.development.ports.DataAssetsServicePort;
import maquette.development.entities.SandboxEntities;
import maquette.development.entities.WorkspaceEntities;

public final class WorkspaceServicesFactory {

    private WorkspaceServicesFactory() {

    }

    public static WorkspaceServices apply(WorkspaceEntities workspaces, DataAssetsServicePort provider) {
        var companion = WorkspaceServicesCompanion.apply(workspaces);
        var impl = WorkspaceServicesImpl.apply(workspaces, provider);
        return WorkspaceServicesSecured.apply(impl, companion);
    }

}
