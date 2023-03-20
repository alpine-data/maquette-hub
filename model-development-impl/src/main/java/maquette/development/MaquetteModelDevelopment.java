package maquette.development;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.MaquetteModule;
import maquette.core.modules.users.UserModule;
import maquette.core.scheduler.model.CronExpression;
import maquette.core.server.commands.Command;
import maquette.core.values.UID;
import maquette.development.commands.*;
import maquette.development.commands.admin.RedeployInfrastructure;
import maquette.development.commands.applications.CreateApplicationCommand;
import maquette.development.commands.applications.ListApplicationsCommand;
import maquette.development.commands.applications.OauthGetSelfCommand;
import maquette.development.commands.applications.RemoveApplicationCommand;
import maquette.development.commands.members.GrantWorkspaceMemberCommand;
import maquette.development.commands.members.RevokeWorkspaceMemberCommand;
import maquette.development.commands.models.CreateModelServiceCommand;
import maquette.development.commands.models.GetModelViewCommand;
import maquette.development.commands.models.GetModelsViewCommand;
import maquette.development.commands.sandboxes.*;
import maquette.development.commands.CreateMachineLearningProjectCommand.*;
import maquette.development.configuration.ModelDevelopmentConfiguration;
import maquette.development.entities.SandboxEntities;
import maquette.development.entities.WorkspaceEntities;
import maquette.development.ports.DataAssetsServicePort;
import maquette.development.ports.ModelsRepository;
import maquette.development.ports.SandboxesRepository;
import maquette.development.ports.WorkspacesRepository;
import maquette.development.ports.infrastructure.InfrastructurePort;
import maquette.development.ports.mlprojects.MLProjectCreationPort;
import maquette.development.ports.models.ModelOperationsPort;
import maquette.development.ports.models.ModelServingPort;
import maquette.development.services.SandboxServices;
import maquette.development.services.WorkspaceServices;
import maquette.development.services.WorkspaceServicesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@AllArgsConstructor(staticName = "apply")
public final class MaquetteModelDevelopment implements MaquetteModule {

    private static final Logger LOG = LoggerFactory.getLogger(MaquetteModelDevelopment.class);

    public static final String MODULE_NAME = "model-development";

    private final ModelDevelopmentConfiguration configuration;

    private final WorkspaceEntities workspaces;

    private final SandboxEntities sandboxes;

    private final DataAssetsServicePort dataAssets;

    private final ModelOperationsPort modelOperations;

    private MaquetteRuntime runtime;


    public static MaquetteModelDevelopment apply(
        MaquetteRuntime runtime,
        WorkspacesRepository workspacesRepository,
        ModelsRepository modelsRepository,
        SandboxesRepository sandboxesRepository,
        InfrastructurePort infrastructurePort,
        DataAssetsServicePort dataAssets,
        ModelOperationsPort modelOperations,
        ModelServingPort modelServing,
        MLProjectCreationPort mlProjects) {

        var configuration = ModelDevelopmentConfiguration.apply();


        var workspaces = WorkspaceEntities.apply(
            workspacesRepository,
            modelsRepository,
            infrastructurePort,
            modelServing,
            mlProjects);

        var sandboxes = SandboxEntities.apply(workspacesRepository, sandboxesRepository, infrastructurePort,
            configuration.getStacks());

        return apply(configuration, workspaces, sandboxes, dataAssets, modelOperations, runtime);
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public void start(MaquetteRuntime runtime) {
        MaquetteModule.super.start(runtime);

        this.runtime = runtime;

        if (configuration.getMlflow().isSyncEnabled()) {
            runtime.getScheduler().schedule(
                "workspaces--update-models",
                CronExpression.apply(configuration.getMlflow().getSyncCron()),
                () -> {
                    LOG.info("Running updates of Model information from MLflow instances.");
                    this
                        .workspaces
                        .refreshModelInformationFromMlflow()
                        .thenRun(() -> LOG.info("Finished updates of Model information from MLflow instances."));
                }
            );
        }

        runtime
            .getApp()
            .get("/api/sandboxes/login", ctx -> {
                var workspace = UID.apply(ctx.queryParam("workspace"));
                var sandbox = UID.apply(ctx.queryParam("sandbox"));
                var stackHash = ctx.queryParam("hash");

                var result = getSandboxServices()
                    .getAuthenticationToken(workspace, sandbox, stackHash)
                    .toCompletableFuture();

                ctx.json(result);
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

        commands.put("workspaces create-ml-project", CreateMachineLearningProjectCommand.class);

        commands.put("workspaces members grant", GrantWorkspaceMemberCommand.class);
        commands.put("workspaces members revoke", RevokeWorkspaceMemberCommand.class);

        commands.put("workspaces applications create", CreateApplicationCommand.class);
        commands.put("workspaces applications remove", RemoveApplicationCommand.class);
        commands.put("workspaces applications oauth-self", OauthGetSelfCommand.class);
        commands.put("workspaces applications list", ListApplicationsCommand.class);

        commands.put("workspaces models view", GetModelsViewCommand.class);
        commands.put("workspaces model view", GetModelViewCommand.class);
        commands.put("workspaces models create-service", CreateModelServiceCommand.class);

        commands.put("workspaces admin redeploy", RedeployInfrastructure.class);

        commands.put("workspaces mlprojects create-mlproject", CreateMachineLearningProjectCommand.class);

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
        return WorkspaceServicesFactory.createSandboxServices(
            workspaces, dataAssets, modelOperations, sandboxes, runtime
                .getModule(UserModule.class)
                .getUsers()
        );
    }

    public WorkspaceServices getWorkspaceServices() {
        var users = runtime.getModule(UserModule.class).getUsers();

        return WorkspaceServicesFactory.createWorkspaceServices(
            workspaces, dataAssets, modelOperations, sandboxes, users, configuration
        );
    }

    public WorkspaceEntities getWorkspaces() {
        return workspaces;
    }

}
