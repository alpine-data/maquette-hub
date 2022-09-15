package maquette.development.commands.admin;

import lombok.AllArgsConstructor;
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
public class RedeployInfrastructure implements Command {

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteModelDevelopment.class)
            .getWorkspaceServices()
            .redeployInfrastructure(user)
            .thenApply(done -> MessageResult.apply("Successfully initiated re-deployment."));
    }

    @Override
    public Command example() {
        return RedeployInfrastructure.apply();
    }

}
