package maquette.development.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.common.validation.api.FluentValidation;
import maquette.core.common.validation.validators.NotNullValidator;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RevokeWorkspaceMemberRoleCommand implements Command {

    String workspace;

    Authorization authorization;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        FluentValidation
            .apply()
            .validate("authorization", authorization, NotNullValidator.apply())
            .checkAndThrow();

        assert authorization != null;

        return runtime.getModule(MaquetteModelDevelopment.class)
            .getServices()
            .revoke(user, workspace, authorization)
            .thenApply(done -> MessageResult.create("Revoked access from `%s`", authorization.getName()));
    }

    @Override
    public Command example() {
        return apply("some-workspace", UserAuthorization.apply("some-user"));
    }

}
