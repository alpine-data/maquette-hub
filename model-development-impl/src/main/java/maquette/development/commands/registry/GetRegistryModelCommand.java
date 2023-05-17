package maquette.development.commands.registry;

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
import maquette.development.commands.views.CentralModelView;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GetRegistryModelCommand implements Command {
    String modelName;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {

        var services = runtime.getModule(MaquetteModelDevelopment.class);

        var workspaceCS = services
            .getCentralModelRegistryServices()
            .getWorkspaceForView();
        var modelCS = services
            .getCentralModelRegistryServices()
            .getModel(user, modelName);

        return Operators.compose(modelCS, workspaceCS, CentralModelView::apply);

    }

    @Override
    public Command example() {
        return apply("some-model");
    }
}
