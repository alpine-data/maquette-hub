package maquette.development.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.DataResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GetWorkspaceCommand implements Command {

    String workspace;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime.getModule(MaquetteModelDevelopment.class)
            .getServices()
            .get(user, workspace)
            .thenApply(DataResult::apply);
    }

    @Override
    public Command example() {
        return apply("some-workspace");
    }

}
