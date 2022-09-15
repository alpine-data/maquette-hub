package maquette.development.commands.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RevokeModelMemberRoleCommand implements Command {

    String workspace;

    String model;

    UserAuthorization authorization;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteModelDevelopment.class)
            .getWorkspaceServices()
            .revokeModelRole(user, workspace, model, authorization)
            .thenApply(done -> MessageResult.apply("Successfully revoked role."));
    }

    @Override
    public Command example() {
        return apply("some-workspace", "some-model", UserAuthorization.apply("edgar"));
    }

}
