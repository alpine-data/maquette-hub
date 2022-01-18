package maquette.development.commands.sandboxes;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GetSandboxCommand implements Command {

    String workspace;

    String sandbox;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime.getModule(MaquetteModelDevelopment.class)
            .getSandboxServices()
            .getSandbox(user, workspace, sandbox)
            .thenApply(DataResult::apply);
    }

    @Override
    public Command example() {
        return GetSandboxCommand.apply("some-project", "some-sandbox");
    }

}
