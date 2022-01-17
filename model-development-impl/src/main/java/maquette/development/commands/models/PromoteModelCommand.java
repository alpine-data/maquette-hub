package maquette.development.commands.models;

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
public class PromoteModelCommand implements Command {

    String workspace;

    String model;

    String version;

    String stage;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime.getModule(MaquetteModelDevelopment.class)
            .getWorkspaceServices()
            .promoteModel(user, workspace, model, version, stage)
            .thenApply(pid -> MessageResult.apply("Successfully approved model version"));
    }

    @Override
    public Command example() {
        return apply("some-workspace", "model", "1.0", "uat");
    }

}
