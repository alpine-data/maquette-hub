package maquette.development;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.MaquetteModule;
import maquette.core.modules.users.UserModule;
import maquette.core.server.commands.Command;
import maquette.core.values.UID;
import maquette.development.commands.*;
import maquette.development.commands.admin.RedeployInfrastructure;
import maquette.development.commands.members.GrantWorkspaceMemberCommand;
import maquette.development.commands.members.RevokeWorkspaceMemberCommand;
import maquette.development.commands.models.GetModelsViewCommand;
import maquette.development.commands.sandboxes.*;
import maquette.development.entities.SandboxEntities;
import maquette.development.entities.WorkspaceEntities;
import maquette.development.ports.DataAssetsServicePort;
import maquette.development.ports.ModelsRepository;
import maquette.development.ports.SandboxesRepository;
import maquette.development.ports.WorkspacesRepository;
import maquette.development.ports.infrastructure.InfrastructurePort;
import maquette.development.services.SandboxServices;
import maquette.development.services.WorkspaceServices;
import maquette.development.services.WorkspaceServicesFactory;

import java.util.Map;

@AllArgsConstructor(staticName = "apply")
public class MaquetteModelDevelopment implements MaquetteModule {

    public static final String MODULE_NAME = "model-development";

    private final WorkspaceEntities workspaces;

    private final WorkspaceServices workspaceServices;

    private final SandboxEntities sandboxes;

    private final InfrastructurePort infrastructurePort;

    private final DataAssetsServicePort dataAssets;

    private final MaquetteRuntime runtime;


    public static MaquetteModelDevelopment apply(
        MaquetteRuntime runtime, WorkspacesRepository workspacesRepository, ModelsRepository modelsRepository, SandboxesRepository sandboxesRepository,
        InfrastructurePort infrastructurePort, DataAssetsServicePort dataAssets) {

        var workspaces = WorkspaceEntities.apply(workspacesRepository, modelsRepository, infrastructurePort);
        var sandboxes = SandboxEntities.apply(workspacesRepository, sandboxesRepository, infrastructurePort);

        var workspaceServices = WorkspaceServicesFactory.createWorkspaceServices(workspaces, dataAssets, sandboxes);
        return apply(workspaces, workspaceServices, sandboxes, infrastructurePort, dataAssets, runtime);
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public void start(MaquetteRuntime runtime) {
        MaquetteModule.super.start(runtime);

        runtime
            .getApp()
            .get("/api/sandboxes/login", ctx -> {
                var workspace = UID.apply(ctx.queryParam("workspace"));
                var sandbox = UID.apply(ctx.queryParam("sandbox"));
                var stackHash = ctx.queryParam("hash");

                ctx.json(getSandboxServices().getAuthenticationToken(workspace, sandbox, stackHash));
            });
    }

    @Override
    public void stop() {
        MaquetteModule.super.stop();
    }

    @Override
    public Map<String, Class<? extends Command>> getCommands() {
        var commands = Maps.<String, Class<? extends Command>>newHashMap();
        commands.put("workspaces create", CreateWorkspaceCommand.class);
        commands.put("workspaces environment", GetWorkspaceEnvironmentCommand.class);
        commands.put("workspaces get", GetWorkspaceCommand.class);
        commands.put("workspaces list", ListWorkspacesCommand.class);
        commands.put("workspaces remove", RemoveWorkspaceCommand.class);
        commands.put("workspaces update", UpdateWorkspaceCommand.class);
        commands.put("workspaces view", WorkspaceViewCommand.class);

        commands.put("workspaces members grant", GrantWorkspaceMemberCommand.class);
        commands.put("workspaces members revoke", RevokeWorkspaceMemberCommand.class);

        commands.put("workspaces models view", GetModelsViewCommand.class);

        commands.put("workspaces admin redeploy", RedeployInfrastructure.class);

        commands.put("sandboxes create", CreateSandboxCommand.class);
        commands.put("sandboxes get", GetSandboxCommand.class);
        commands.put("sandboxes stacks", GetStacksCommand.class);
        commands.put("sandboxes list", ListSandboxesCommand.class);
        commands.put("sandboxes remove", RemoveSandboxCommand.class);
        return commands;
    }

    public SandboxEntities getSandboxes() {
        return sandboxes;
    }

    public SandboxServices getSandboxServices() {
        return WorkspaceServicesFactory.createSandboxServices(workspaces, dataAssets, sandboxes, runtime.getModule(UserModule.class).getUsers());
    }

    public WorkspaceServices getWorkspaceServices() {
        return workspaceServices;
    }

    public WorkspaceEntities getWorkspaces() {
        return workspaces;
    }

}
