package maquette.operations;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.MaquetteModule;
import maquette.core.server.commands.Command;
import maquette.operations.commands.RegisterDeployedModelServiceInstanceCommand;
import maquette.operations.entities.DeployedModelServiceEntities;
import maquette.operations.ports.DeployedModelServicesRepository;
import maquette.operations.ports.ModelDevelopmentPort;
import maquette.operations.services.DeployedModelServices;
import maquette.operations.services.DeployedModelServicesImpl;

import java.util.Map;

@AllArgsConstructor(staticName = "apply")
public class MaquetteModelOperations implements MaquetteModule {

    public static final String MODULE_NAME = "model-operations";

    private final DeployedModelServiceEntities entities;

    private final DeployedModelServices deployedModelServices;

    public static MaquetteModelOperations apply(DeployedModelServicesRepository deployedModelServicesRepository, ModelDevelopmentPort modelDevelopmentPort) {
        var entities = DeployedModelServiceEntities.apply(deployedModelServicesRepository);
        var services = DeployedModelServicesImpl.apply(entities, modelDevelopmentPort);

        return MaquetteModelOperations.apply(entities, services);
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
        commands.put("operations register-model-service-instance", RegisterDeployedModelServiceInstanceCommand.class);
        return commands;
    }

    public DeployedModelServiceEntities getEntities() {
        return entities;
    }

    public DeployedModelServices getServices() {
        return deployedModelServices;
    }

}
