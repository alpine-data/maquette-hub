package maquette.development.commands.applications;

import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.DataResult;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;

import java.util.concurrent.CompletionStage;


@AllArgsConstructor(staticName = "apply")
public class OauthGetSelfCommand implements Command {

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteModelDevelopment.class)
            .getWorkspaceServices()
            .getOauthSelfApplication(runtime, user)
            .thenApply(DataResult::apply);
    }


    @Override
    public Command example() {
        return apply();
    }
}
