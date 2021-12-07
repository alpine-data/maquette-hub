package maquette.development.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
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
        return runtime.getModule(MaquetteModelDevelopment.class)
            .getServices()
            .get(user, name)
            .thenApply(WorkspaceView::apply);
    }

    @Override
    public Command example() {
        return WorkspaceViewCommand.apply("some-workspace");
    }
}
