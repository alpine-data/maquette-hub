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
import maquette.operations.value.DeployedModelVersion;
import maquette.operations.value.RegisterDeployedModelServiceInstanceParameters;
import maquette.operations.value.RegisterDeployedModelServiceParameters;

import java.util.Set;
import java.util.concurrent.CompletionStage;

/**
 * This command is intended to be called from a DevOps Pipeline to register/ inform about a
 * model deployment.
 *
 * A model is deployed within a service. Each service can have multiple instance (e.g. environments). Thus
 * model information are passed within instance properties. Because different model versions can be deployed
 * on different environments.
 *
 * Each service (instance) might contain multiple different models. Thus, an instance can inform about deployment
 * of multiple models.
 */
@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RegisterDeployedModelServiceInstanceCommand implements Command {

    /**
     * Information about the service which has been deployed.
     */
    RegisterDeployedModelServiceParameters service;

    /**
     * Information about the instance of a service.
     */
    RegisterDeployedModelServiceInstanceParameters instance;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteModelOperations.class)
            .getServices()
            .registerModelServiceInstance(user, service, instance)
            .thenApply(result -> MessageResult.apply(
                "Successfully registered model service instance."
            ));
    }

    @Override
    public Command example() {
        return apply(
            RegisterDeployedModelServiceParameters.apply("some-service", "http://foo", "http://bar"),
            RegisterDeployedModelServiceInstanceParameters.apply(
                "http://service.com",
                Set.of(DeployedModelVersion.apply("mlflow_instance_id/name", "1")),
                "DEVSIT")
        );
    }

}
