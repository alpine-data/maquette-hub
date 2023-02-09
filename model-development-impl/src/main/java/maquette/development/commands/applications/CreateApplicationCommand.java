package maquette.development.commands.applications;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;

import java.util.concurrent.CompletionStage;


@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateApplicationCommand implements Command {

    String workspace;
    String application;
    String metaInfo;


    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteModelDevelopment.class)
            .getWorkspaceServices()
            .createApplication(runtime, user, workspace, application, metaInfo)
            .thenApply(done -> MessageResult.create("Successfully created application."));
    }


    @Override
    public Command example() {
        return apply("workspace-1", "Application 1", "{}");
    }
}
