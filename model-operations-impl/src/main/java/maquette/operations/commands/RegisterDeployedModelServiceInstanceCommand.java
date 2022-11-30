package maquette.operations.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.user.User;
import maquette.operations.MaquetteModelOperations;
import maquette.operations.value.DeployedModelProperties;
import maquette.operations.value.DeployedModelServiceInstanceProperties;
import maquette.operations.value.DeployedModelServiceProperties;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RegisterDeployedModelServiceInstanceCommand implements Command {

    DeployedModelProperties model;

    DeployedModelServiceProperties service;

    DeployedModelServiceInstanceProperties instance;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteModelOperations.class)
            .getServices()
            .registerModelServiceInstance(user, model, service, instance)
            .thenApply(result -> MessageResult.apply("Successfully registered."));
    }

    @Override
    public Command example() {
        return apply(
            DeployedModelProperties.apply("model_name", "http://some-mlwflow/models/model_name"),
            DeployedModelServiceProperties.apply("some-service", "http://foo", "http://bar", "http://lorem"),
            DeployedModelServiceInstanceProperties.apply("http://service.com", "1.0.2", "sit")
        );
    }

}
