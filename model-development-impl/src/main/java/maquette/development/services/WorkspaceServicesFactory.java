package maquette.development.services;

import maquette.core.modules.users.UserEntities;
import maquette.core.modules.users.services.UserCompanion;
import maquette.core.modules.users.services.UserServicesFactory;
import maquette.development.configuration.ModelDevelopmentConfiguration;
import maquette.development.entities.SandboxEntities;
import maquette.development.entities.WorkspaceEntities;
import maquette.development.ports.DataAssetsServicePort;
import maquette.development.ports.models.ModelOperationsPort;

public final class WorkspaceServicesFactory {

    private WorkspaceServicesFactory() {

    }

    public static WorkspaceServices createWorkspaceServices(
        WorkspaceEntities workspaces,
        DataAssetsServicePort provider,
        ModelOperationsPort modelOperations,
        SandboxEntities sandboxes,
        UserEntities users,
        ModelDevelopmentConfiguration configuration) {

        var companion = WorkspaceServicesCompanion.apply(workspaces, sandboxes);
        var impl = WorkspaceServicesImpl.apply(workspaces, sandboxes, users, provider, modelOperations);
        var secured = WorkspaceServicesSecured.apply(impl, companion);
        return WorkspaceServicesValidated.apply(secured, configuration);
    }

    public static SandboxServices createSandboxServices(
        WorkspaceEntities workspaces, DataAssetsServicePort provider, ModelOperationsPort modelOperations,
        SandboxEntities sandboxes, UserEntities users) {

        var workspacesCompanion = WorkspaceServicesCompanion.apply(workspaces, sandboxes);
        var userServices = UserServicesFactory.apply(users);

        var impl = SandboxServicesImpl.apply(sandboxes, workspaces, users, UserCompanion.apply(users));
        var secured = SandboxServicesSecured.apply(impl, userServices, workspacesCompanion);
        return SandboxServicesValidated.apply(secured);
    }

}
