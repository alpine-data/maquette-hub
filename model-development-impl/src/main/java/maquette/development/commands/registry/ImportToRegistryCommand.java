package maquette.development.commands.registry;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ImportToRegistryCommand implements Command {
    String workspace;
    String model;
    String version;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteModelDevelopment.class)
            .getCentralModelRegistryServices()
            .importModel(user, workspace, model, version)
            .thenApply(done -> MessageResult.apply("Successfully imported model into central model registry"));
    }

    @Override
    public Command example() {
        return apply("my-workspace", "my-model", "1");
    }

}