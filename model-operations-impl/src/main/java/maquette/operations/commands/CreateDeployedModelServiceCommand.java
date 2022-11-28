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
import maquette.operations.value.DeployedModelServiceProperties;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateDeployedModelServiceCommand implements Command {

    String modelName;

    DeployedModelServiceProperties properties;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteModelOperations.class)
            .getDeployedModelServices()
            .createDeployedModelService(user, modelName, properties)
            .thenApply(result -> MessageResult.apply("successfuly created"));
    }

    @Override
    public Command example() {
        return apply("some-model", DeployedModelServiceProperties.apply("some-model-service", "git://some-repo/model" +
            ".git", "https://backstage-zurich/model-service", "https://dev.azure.com/pipelines/some-pipeline"));
    }
}
