package maquette.development.commands.registry;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
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
public class GetRegistryModelsCommand implements Command {
    String query;

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        return runtime
            .getModule(MaquetteModelDevelopment.class)
            .getCentralModelRegistryServices()
            .getModels(user, query)
            .thenApply(models -> ModelsView.apply(models, ModelPermissions.apply(false, false, false)));
    }

    @Override
    public Command example() {
        return apply("some-model");
    }

}
