package maquette.development.commands.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.DataResult;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.User;
import maquette.development.MaquetteModelDevelopment;
import maquette.development.values.model.ModelMemberRole;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GrantModelMemberRoleCommand implements Command {

    String workspace;

    String model;

    UserAuthorization authorization;

    ModelMemberRole role;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime.getModule(MaquetteModelDevelopment.class)
            .getServices()
            .getModels(user, workspace)
            .thenApply(DataResult::apply);
    }

    @Override
    public Command example() {
        return apply("some-workspace", "model", UserAuthorization.apply("ok"), ModelMemberRole.DATA_SCIENTIST);
    }

}
