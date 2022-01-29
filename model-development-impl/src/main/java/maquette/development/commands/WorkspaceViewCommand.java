package maquette.development.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;
import maquette.development.commands.views.WorkspaceView;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class WorkspaceViewCommand implements Command {

    String name;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        var services = runtime.getModule(MaquetteModelDevelopment.class);

        var workspaceCS = services
            .getWorkspaceServices()
            .get(user, name);

        var stacksCS = services
            .getSandboxServices()
            .getStacks(user);

        return Operators.compose(workspaceCS, stacksCS, WorkspaceView::apply);
    }

    @Override
    public Command example() {
        return WorkspaceViewCommand.apply("some-workspace");
    }

}
