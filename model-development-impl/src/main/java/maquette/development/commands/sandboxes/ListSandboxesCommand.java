package maquette.development.commands.sandboxes;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.DataResult;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
public class ListSandboxesCommand implements Command {

    String workspace;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime.getModule(MaquetteModelDevelopment.class)
            .getSandboxServices()
            .getSandboxes(user, workspace)
            .thenApply(DataResult::apply);
    }

    @Override
    public Command example() {
        return ListSandboxesCommand.apply("some-workspace");
    }

}
