package maquette.development.commands.models;

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
import maquette.development.commands.views.ModelsView;
import maquette.development.values.model.ModelPermissions;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GetModelsViewCommand implements Command {

    String workspace;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        var workspaces = runtime
            .getModule(MaquetteModelDevelopment.class)
            .getWorkspaceServices();

        var workspacePermissionsCS = workspaces
            .get(user, workspace)
            .thenApply(wks -> wks.getWorkspacePermissions(user));

        var modelsCS = workspaces
            .getModels(user, workspace);

        return Operators.compose(modelsCS, workspacePermissionsCS, (models, workspacePermissions) -> {
            var modelPermissions = ModelPermissions.apply(
                workspacePermissions.isAdmin(),
                workspacePermissions.isAdmin(),
                workspacePermissions.isMember());

            return ModelsView.apply(models, modelPermissions);
        });
    }

    @Override
    public Command example() {
        return apply("some-workspace");
    }

}
