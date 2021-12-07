package maquette.development;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.MaquetteModule;
import maquette.core.server.commands.Command;
import maquette.development.commands.*;
import maquette.development.entities.WorkspaceEntities;
import maquette.development.ports.DataAssetsServicePort;
import maquette.development.ports.InfrastructurePort;
import maquette.development.ports.ModelsRepository;
import maquette.development.ports.WorkspacesRepository;
import maquette.development.services.WorkspaceServices;
import maquette.development.services.WorkspaceServicesFactory;

import java.util.Map;

@AllArgsConstructor(staticName = "apply")
public class MaquetteModelDevelopment implements MaquetteModule {

    public static final String MODULE_NAME = "model-development";

    private final WorkspaceEntities entities;

    private final WorkspaceServices services;

    public static MaquetteModelDevelopment apply(
        WorkspacesRepository workspacesRepository, ModelsRepository modelsRepository,
        InfrastructurePort infrastructurePort, DataAssetsServicePort dataAssets) {

        var entities = WorkspaceEntities.apply(workspacesRepository, modelsRepository, infrastructurePort);
        var services = WorkspaceServicesFactory.apply(entities, dataAssets);

        return apply(entities, services);
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public void start(MaquetteRuntime runtime) {
        MaquetteModule.super.start(runtime);
    }

    @Override
    public void stop() {
        MaquetteModule.super.stop();
    }

    @Override
    public Map<String, Class<? extends Command>> getCommands() {
        var commands = Maps.<String, Class<? extends Command>>newHashMap();
        commands.put("workspaces create", CreateWorkspaceCommand.class);
        commands.put("workspaces update", UpdateWorkspaceCommand.class);
        commands.put("workspaces get", GetWorkspaceCommand.class);
        commands.put("workspaces list", ListWorkspacesCommand.class);
        commands.put("workspaces view", WorkspaceViewCommand.class);
        return commands;
    }

    public WorkspaceServices getServices() {
        return services;
    }

    public WorkspaceEntities getEntities() {
        return entities;
    }

}
