package maquette.operations;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.MaquetteModule;
import maquette.core.server.commands.Command;
import maquette.operations.commands.CreateDeployedModelCommand;
import maquette.operations.commands.CreateDeployedModelServiceCommand;
import maquette.operations.commands.GetDeployedModelCommand;
import maquette.operations.entities.DeployedModelEntities;
import maquette.operations.ports.DeployedModelServicesRepository;
import maquette.operations.ports.DeployedModelsRepository;
import maquette.operations.services.DeployedModelServices;
import maquette.operations.services.DeployedModelServicesImpl;

import java.util.Map;

@AllArgsConstructor(staticName = "apply")
public class MaquetteModelOperations implements MaquetteModule {
    public static final String MODULE_NAME = "model-operations";

    private final DeployedModelServices deployedModelServices;

    public static MaquetteModelOperations apply(DeployedModelsRepository deployedModelsRepository, DeployedModelServicesRepository deployedModelServicesRepository) {
        return MaquetteModelOperations.apply(DeployedModelServicesImpl.apply(DeployedModelEntities.apply(deployedModelsRepository, deployedModelServicesRepository)));
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
        commands.put("operations create-deployed-model", CreateDeployedModelCommand.class);
        commands.put("operations create-deployed-model-service", CreateDeployedModelServiceCommand.class);
        commands.put("operations get-deployed-model", GetDeployedModelCommand.class);
        return commands;
    }

    public DeployedModelServices getDeployedModelServices() {
        return deployedModelServices;
    }
}
