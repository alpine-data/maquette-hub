package maquette.development.commands;

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
public class UpdateWorkspaceCommand implements Command {

    String workspace;

    String name;

    String title;

    String summary;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime.getModule(MaquetteModelDevelopment.class)
            .getServices()
            .update(user, workspace, name, title, summary)
            .thenApply(done -> MessageResult.apply("Successfully updated workspace."));
    }

    @Override
    public Command example() {
        return apply("some-workspace", "some-new-name", "title", "summary");
    }

}
